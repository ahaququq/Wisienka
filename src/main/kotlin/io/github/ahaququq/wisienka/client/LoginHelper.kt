package io.github.ahaququq.wisienka.client

import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Version
import imgui.type.ImString
import io.github.ahaququq.wisienka.Wisienka
import io.github.ahaququq.wisienka.networking.PacketIDs
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf

object LoginHelper {
	private var username: ImString? = null
	private var password: ImString? = null

	fun passwordLogin(username: ImString, password: ImString) {
		this.username = username
		this.password = password

		ClientPlayNetworking.send(
			PacketIDs.LOGIN_READY_PACKET_C2S,
			PacketByteBufs
				.create()
				.writeNbt(
					NbtCompound().also {
						it.putString("Username", username.get())
					}
				)
		)
	}

	fun register(username: ImString, password: ImString, closable: Boolean) {
		this.username = username
		this.password = password

		val argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id, 512, 512)

		// Overkill
		val salt = argon2.generateSalt(512)
		val hash = argon2.hashAdvanced(
			16,
			65536,
			1,
			password.data.sliceArray(0..<password.length),
			salt,
			512,
			Argon2Version.DEFAULT_VERSION
		)

		Wisienka.logger.info("Hash: `${hash.encoded}`")
		Wisienka.logger.info("Hash Raw: `${hash.raw}`")
		Wisienka.logger.info("Salt: `${salt}`")
		Wisienka.logger.info("Verification: ${argon2.verify(hash.encoded, password.data)}")


		if (!closable) {
			ClientPlayNetworking.send(
				PacketIDs.LOGIN_REGISTER_PACKET_C2S,
				PacketByteBufs
					.create()
					.writeNbt(
						NbtCompound().also {
							it.putString("Username", username.get())
							it.putByteArray("Hash", hash.raw)
							it.putByteArray("Salt", salt)
						}
					)
			)
		}
	}

	fun premiumLogin() {

	}

	fun onSalt(
		client: MinecraftClient,
		handler: ClientPlayNetworkHandler,
		buf: PacketByteBuf,
		responseSender: PacketSender
	) {
		val nbt = buf.readNbt()!!
		val salt = nbt.getByteArray("Salt")
		val nonce = nbt.getByteArray("Nonce")

		val argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id, 512, 512)

		val hash = password?.let {
			argon2.hashAdvanced(
				16,
				65536,
				1,
				it.data.sliceArray(0..<it.length),
				salt,
				512,
				Argon2Version.DEFAULT_VERSION
			)
		}

		if (hash == null) {
			client.disconnect()
			return
		}

		val withNonce = hash.raw.zip(nonce, fun(
			a: Byte,
			b: Byte
		): Byte {
			return (a + b).toByte()
		})

		responseSender.sendPacket(
			PacketIDs.LOGIN_HASH_PACKET_C2S,
			PacketByteBufs
				.create()
				.writeNbt(
					NbtCompound().also {
						it.putString("Username", username!!.get())
						it.putByteArray("Hash", withNonce.toByteArray())
					}
				)
		)
	}
}