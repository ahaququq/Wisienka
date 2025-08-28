package io.github.ahaququq.wisienka.networking

import io.github.ahaququq.wisienka.Wisienka.Companion.logger
import io.github.ahaququq.wisienka.client.LoginHelper
import io.github.ahaququq.wisienka.client.screen.LoginScreen
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

object ClientNetworking {
	fun init() {
		ClientPlayConnectionEvents.JOIN.register(fun(
			handler: ClientPlayNetworkHandler,
			sender: PacketSender,
			client: MinecraftClient
		) {
			sender.sendPacket(PacketIDs.LOGIN_HANDSHAKE_PACKET_C2S, PacketByteBufs.empty())
			logger.info("Sent handshake to server")
		})

		ClientPlayNetworking.registerGlobalReceiver(PacketIDs.LOGIN_HANDSHAKE_PACKET_S2C, fun(
			client: MinecraftClient,
			handler: ClientPlayNetworkHandler,
			buf: PacketByteBuf,
			responseSender: PacketSender
		) {
			client.execute {
				client.setScreen(LoginScreen())
			}
		})

		ClientPlayNetworking.registerGlobalReceiver(PacketIDs.LOGIN_SALT_PACKET_S2C, fun(
			client: MinecraftClient,
			handler: ClientPlayNetworkHandler,
			buf: PacketByteBuf,
			responseSender: PacketSender
		) {
			LoginHelper.onSalt(client, handler, buf, responseSender)
		})
	}
}