package io.github.ahaququq.wisienka.networking

import io.github.ahaququq.wisienka.Wisienka.Companion.logger
import io.github.ahaququq.wisienka.login.LoginManager
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object ServerNetworking {
	fun init() {
		ServerTickEvents.START_SERVER_TICK.register {
			LoginManager.tick(it)
		}

		ServerPlayNetworking.registerGlobalReceiver(PacketIDs.LOGIN_HANDSHAKE_PACKET_C2S, fun(
			server: MinecraftServer,
			player: ServerPlayerEntity,
			handler: ServerPlayNetworkHandler,
			buf: PacketByteBuf,
			responseSender: PacketSender
		) {
			logger.info("Received handshake from ${player.name}")
			responseSender.sendPacket(PacketIDs.LOGIN_HANDSHAKE_PACKET_S2C, PacketByteBufs.empty())
		})

		ServerPlayNetworking.registerGlobalReceiver(PacketIDs.LOGIN_CANCEL_PACKET_C2S, fun(
			server: MinecraftServer,
			player: ServerPlayerEntity,
			handler: ServerPlayNetworkHandler,
			buf: PacketByteBuf,
			responseSender: PacketSender
		) {
			logger.info("Received cancellation from ${player.name}")
			handler.disconnect(Text.translatable("wisienka.disconnect.cancel"))
		})

		ServerPlayNetworking.registerGlobalReceiver(PacketIDs.LOGIN_REGISTER_PACKET_C2S, fun(
			server: MinecraftServer,
			player: ServerPlayerEntity,
			handler: ServerPlayNetworkHandler,
			buf: PacketByteBuf,
			responseSender: PacketSender
		) {
			val nbt = buf.readNbt()!!
			val username = nbt.getString("Username")
			val hash = nbt.getByteArray("Hash")
			val salt = nbt.getByteArray("Salt")

			LoginManager.register(player, username, hash, salt, handler, server)
		})

		ServerPlayNetworking.registerGlobalReceiver(PacketIDs.LOGIN_READY_PACKET_C2S, fun(
			server: MinecraftServer,
			player: ServerPlayerEntity,
			handler: ServerPlayNetworkHandler,
			buf: PacketByteBuf,
			responseSender: PacketSender
		) {
			LoginManager.getNonce(server, handler, responseSender, player, buf)
		})

		ServerPlayNetworking.registerGlobalReceiver(PacketIDs.LOGIN_HASH_PACKET_C2S, fun(
			server: MinecraftServer,
			player: ServerPlayerEntity,
			handler: ServerPlayNetworkHandler,
			buf: PacketByteBuf,
			responseSender: PacketSender
		) {
			LoginManager.login(server, player, handler, buf, responseSender)
		})
	}
}
