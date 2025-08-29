package io.github.ahaququq.wisienka.login

import io.github.ahaququq.wisienka.client.screen.ImGuiScreen
import io.github.ahaququq.wisienka.client.screen.ImGuiScreenManager
import io.github.ahaququq.wisienka.login.LoginWindow.loginWindow
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf

object ClientLoginHandler {
	fun init() {
		ClientPlayConnectionEvents.JOIN.register(::onJoin)
		ClientPlayNetworking.registerGlobalReceiver(LoginPacketIDs.handshake, ::onHandshake)
		ClientPlayNetworking.registerGlobalReceiver(LoginPacketIDs.registrationReady, ::onRegistrationReady)
		ClientPlayNetworking.registerGlobalReceiver(LoginPacketIDs.loginReady, ::onLoginReady)
		ClientPlayNetworking.registerGlobalReceiver(LoginPacketIDs.showMenu, ::onShowMenu)
	}

	val emptyBuffer: PacketByteBuf get() = PacketByteBufs.empty()

	fun onJoin(
		handler: ClientPlayNetworkHandler,
		sender: PacketSender,
		client: MinecraftClient
	) {
		sender.sendPacket(LoginPacketIDs.handshake, emptyBuffer)
	}

	fun onHandshake(
		client: MinecraftClient,
		handler: ClientPlayNetworkHandler,
		buf: PacketByteBuf,
		responseSender: PacketSender
	) {
		client.execute { client.setScreen(ImGuiScreen(client.currentScreen)) }
		ImGuiScreenManager.registerRender(::loginWindow)
	}

	private var username: String? = null
	private var password = ByteArray(0)

	fun register(password: ByteArray, username: String) {
		this.password = password
		this.username = username
		ClientPlayNetworking.send(
			LoginPacketIDs.registrationReady,
			PacketByteBufs
				.create()
				.writeNbt(NbtCompound().also {
					it.putString("Username", username)
				})
		)
	}

	fun onRegistrationReady(
		client: MinecraftClient,
		handler: ClientPlayNetworkHandler,
		buf: PacketByteBuf,
		responseSender: PacketSender
	) {
		val (hash, salt) = AuthInfo(password)

		responseSender.sendPacket(
			LoginPacketIDs.register,
			PacketByteBufs
				.create()
				.writeNbt(NbtCompound().also {
					it.putString("Username", username)
					it.putByteArray("Hash", hash)
					it.putByteArray("Salt", salt)
				})
		)
	}

	fun login(password: ByteArray, username: String) {
		this.password = password
		this.username = username
		ClientPlayNetworking.send(
			LoginPacketIDs.loginReady,
			PacketByteBufs
				.create()
				.writeNbt(NbtCompound().also {
					it.putString("Username", username)
				})
		)
	}

	fun onLoginReady(
		client: MinecraftClient,
		handler: ClientPlayNetworkHandler,
		buf: PacketByteBuf,
		responseSender: PacketSender
	) {
		val nbt = buf.readNbt()!!

		val nonce = nbt.getByteArray("Nonce")
		val salt = nbt.getByteArray("Salt")

		responseSender.sendPacket(
			LoginPacketIDs.login,
			PacketByteBufs
				.create()
				.writeNbt(NbtCompound().also {
					it.putByteArray("Hash", AuthInfo.hashWithNonce(password, salt, nonce))
				})
		)
	}

	fun onShowMenu(
		client: MinecraftClient,
		handler: ClientPlayNetworkHandler,
		buf: PacketByteBuf,
		responseSender: PacketSender
	) {
		client.execute {
			client.setScreen(null)
		}
	}
}