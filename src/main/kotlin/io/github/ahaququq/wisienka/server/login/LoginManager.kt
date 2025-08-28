package io.github.ahaququq.wisienka.server.login

import com.mojang.authlib.GameProfile
import io.github.ahaququq.wisienka.Wisienka
import io.github.ahaququq.wisienka.networking.PacketIDs
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.security.SecureRandom
import java.util.*

object LoginManager {
	class PlayerEntry(
		var originalProfile: GameProfile?,
		var newProfile: GameProfile,
		var premium: Boolean,
		var state: State = State.LOGGING_IN,
		var nonce: ByteArray = ByteArray(512) { 0 },
		var age: Int = 0
	) {
		enum class State {
			LOGGING_IN,
			NONCE,
			SALT,
			PLAYING,
		}
	}

	val players = mutableListOf<PlayerEntry>()

	fun newPlayer(
		originalProfile: GameProfile?,
		premium: Boolean
	): GameProfile {
		val uuid = UUID.randomUUID()
		val playerEntry = PlayerEntry(
			originalProfile,
			GameProfile(uuid, "%$uuid".substring(0..15)),
			premium
		)
		players.add(playerEntry)
		return playerEntry.newProfile
	}

	fun shouldChangeSpawn(profile: GameProfile): Boolean {
		return !(players.filter { it.newProfile == profile }.all { it.state == PlayerEntry.State.PLAYING })
	}

	fun remove(profile: GameProfile, server: MinecraftServer) {
//		players.filter { it.newProfile == profile }.forEach {
//			server.playerManager.getPlayer(it.newProfile.id)?.networkHandler?.disconnect(Text.of("Login took too long!"))
//		}
		players.removeIf { it.newProfile == profile }
	}

	data class Account(val username: String, val hash: ByteArray, val salt: ByteArray, val premiumProfile: GameProfile?) {
		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false

			other as Account

			if (username != other.username) return false
			if (!hash.contentEquals(other.hash)) return false
			if (!salt.contentEquals(other.salt)) return false
			if (premiumProfile != other.premiumProfile) return false

			return true
		}

		override fun hashCode(): Int {
			var result = username.hashCode()
			result = 31 * result + hash.contentHashCode()
			result = 31 * result + salt.contentHashCode()
			result = 31 * result + (premiumProfile?.hashCode() ?: 0)
			return result
		}
	}

	val accounts = mutableListOf<Account>()

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
			entry.originalProfile
		))

		entry.state = PlayerEntry.State.PLAYING
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
				players.remove(it)
				server.playerManager.getPlayer(it.newProfile.id)?.networkHandler?.disconnect(Text.of("Login took too long!"))
			}
		}
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

		val withNonce = hash.zip(nonce, fun(
			a: Byte,
			b: Byte
		): Byte {
			return (a + b).toByte()
		})

		val recieved = nbt.getByteArray("Hash")

		val matching = recieved.zip(withNonce) { a, b -> a == b }.all { it }

		Wisienka.logger.info("Matching: $matching")

		handler.disconnect(Text.of("Logged in!"))
		remove(player.gameProfile, server)
	}
}