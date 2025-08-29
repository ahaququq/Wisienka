package io.github.ahaququq.wisienka.login

import com.mojang.authlib.GameProfile
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Version
import io.github.ahaququq.wisienka.Wisienka
import io.github.ahaququq.wisienka.networking.PacketIDs
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.PositionFlag
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.world.World
import java.security.SecureRandom
import java.util.*

object LoginManager {
	class PlayerEntry(
		var originalProfile: GameProfile?,
		var newProfile: GameProfile,
		var state: State = State.LOGGING_IN,
		var nonce: ByteArray = ByteArray(512) { 0 },
		var age: Int = 0
	) {
		val premium: Boolean
			get() = originalProfile == null

		enum class State(val changeSpawn: Boolean) {
			LOGGING_IN(true),
			SALT(true),
			MENU(true),
			PLAYING(false),
		}
	}

	var players = mutableListOf<PlayerEntry>()

	fun newProfile(): GameProfile = UUID.randomUUID().run { GameProfile(this, "%$this".substring(0..15)) }

	fun newPlayer(originalProfile: GameProfile?): GameProfile {
		val playerEntry = PlayerEntry(originalProfile, newProfile())
		players.add(playerEntry)
		return playerEntry.newProfile
	}

	fun shouldChangeSpawn(profile: GameProfile) = players.find { it.newProfile == profile }?.state?.changeSpawn ?: false
	fun remove(profile: GameProfile, server: MinecraftServer) = players.removeIf { it.newProfile == profile }

	data class Account(
		val username: String,
		val hash: ByteArray,
		val salt: ByteArray,
		val premiumProfile: GameProfile?,
		val menuProfile: GameProfile?
	) {
		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false

			other as Account

			if (username != other.username) return false
			if (!hash.contentEquals(other.hash)) return false
			if (!salt.contentEquals(other.salt)) return false
			if (premiumProfile != other.premiumProfile) return false
			if (menuProfile != other.menuProfile) return false

			return true
		}

		override fun hashCode(): Int {
			var result = username.hashCode()
			result = 31 * result + hash.contentHashCode()
			result = 31 * result + salt.contentHashCode()
			result = 31 * result + (premiumProfile?.hashCode() ?: 0)
			result = 31 * result + (menuProfile?.hashCode() ?: 0)
			return result
		}
	}

	val accounts = mutableListOf<Account>()

	fun openMenu(entry: PlayerEntry, player: ServerPlayerEntity, account: Account) {
		entry.state = PlayerEntry.State.MENU
	}

	private fun playerEntry(player: ServerPlayerEntity): PlayerEntry? {
		val entry = players.filter {
			it.newProfile == player.gameProfile
		}.getOrNull(0)
		return entry
	}

	fun register(
		player: ServerPlayerEntity,
		username: String,
		hash: ByteArray,
		salt: ByteArray,
		handler: ServerPlayNetworkHandler,
		server: MinecraftServer
	) {
		val entry = playerEntry(player)

		if (entry == null) {
			handler.disconnect(Text.of("Player entry not found!"))
			remove(player.gameProfile, server)
			return
		}

		if (entry.state != PlayerEntry.State.LOGGING_IN) {
			handler.disconnect(Text.of("Player entry not logging in!"))
			remove(player.gameProfile, server)
			return
		}

		accounts.add(Account(
			username,
			hash,
			salt,
			entry.originalProfile,
			null
		))

		val account = accounts.last()

		openMenu(entry, player, account)
	}

	fun getNonce(
		server: MinecraftServer,
		handler: ServerPlayNetworkHandler,
		responseSender: PacketSender,
		player: ServerPlayerEntity,
		buf: PacketByteBuf
	) {
		val entry = playerEntry(player)
		val username = buf.readNbt()!!.getString("Username")

		if (entry == null) {
			handler.disconnect(Text.of("Player entry not found!"))
			remove(player.gameProfile, server)
			return
		}

		if (entry.state != PlayerEntry.State.LOGGING_IN) {
			handler.disconnect(Text.of("Player entry not logging in!"))
			remove(player.gameProfile, server)
			return
		}

		SecureRandom.getInstanceStrong().nextBytes(entry.nonce)

		val salt = accounts.find { it.username == username }?.salt

		if (salt == null) {
			handler.disconnect(Text.of("Account not found!"))
			remove(player.gameProfile, server)
			return
		}

		responseSender.sendPacket(
			PacketIDs.LOGIN_SALT_PACKET_S2C,
			PacketByteBufs
				.create()
				.writeNbt(
					NbtCompound().also {
						it.putByteArray("Nonce", entry.nonce)
						it.putByteArray("Salt", salt)
					}
				)
		)

		entry.state = PlayerEntry.State.SALT
	}

	fun tick(server: MinecraftServer) {
		for (it in players) {
			if (it.age++ > 20 * 60 * 5) {
				server.playerManager.getPlayer(it.newProfile.id)?.networkHandler?.disconnect(Text.of("Login took too long!"))
			}
		}
		players.removeIf { it.age > 20 * 60 * 5 }
	}

	fun login(
		server: MinecraftServer,
		player: ServerPlayerEntity,
		handler: ServerPlayNetworkHandler,
		buf: PacketByteBuf,
		responseSender: PacketSender
	) {
		val entry = playerEntry(player)

		if (entry == null) {
			handler.disconnect(Text.of("Player entry not found!"))
			remove(player.gameProfile, server)
			return
		}

		if (entry.state != PlayerEntry.State.SALT) {
			handler.disconnect(Text.of("Player entry not in nonce state!"))
			remove(player.gameProfile, server)
			return
		}

		val nbt = buf.readNbt()!!
		val username = nbt.getString("Username")
		val account = accounts.find { it.username == username }

		if (account == null) {
			handler.disconnect(Text.of("Account not found!"))
			remove(player.gameProfile, server)
			return
		}

		val hash = account.hash
		val salt = account.salt
		val nonce = entry.nonce

		val argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id, 512, 512)

		val hash2 = argon2.hashAdvanced(
			1,
			4096,
			1,
			hash,
			nonce,
			512,
			Argon2Version.DEFAULT_VERSION
		)

		val recieved = nbt.getByteArray("Hash")

		val matching = recieved.zip(hash2.raw) { a, b -> a == b }.all { it }

		Wisienka.logger.info("Matching: $matching")

		if (!matching) {
			handler.disconnect(Text.of("Wrong password!"))
			remove(player.gameProfile, server)
			return
		}

		entry.state = PlayerEntry.State.PLAYING

		responseSender.sendPacket(PacketIDs.LOGIN_FINISHED_PACKET_S2C, PacketByteBufs.empty())

		player.setSpawnPoint(
			World.OVERWORLD,
			server.overworld.spawnPos,
			server.overworld.spawnAngle,
			true,
			false
		)
		player.teleport(
			server.overworld,
			server.overworld.spawnPos.x.toDouble(),
			server.overworld.spawnPos.y.toDouble(),
			server.overworld.spawnPos.z.toDouble(),
			setOf<PositionFlag>(),
			0.0f,
			0.0f,
		)
	}
}