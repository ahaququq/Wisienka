package io.github.ahaququq.wisienka.login

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.security.SecureRandom

object ServerLoginHandler {
	fun init() {
		ServerPlayConnectionEvents.DISCONNECT.register(::onDisconnect)

		ServerPlayNetworking.registerGlobalReceiver(LoginPacketIDs.handshake, ::onHandshake)
		ServerPlayNetworking.registerGlobalReceiver(LoginPacketIDs.registrationReady, ::onRegisterReady)
		ServerPlayNetworking.registerGlobalReceiver(LoginPacketIDs.register, ::onRegister)
		ServerPlayNetworking.registerGlobalReceiver(LoginPacketIDs.loginReady, ::onLoginReady)
		ServerPlayNetworking.registerGlobalReceiver(LoginPacketIDs.login, ::onLogin)
	}

	val emptyBuffer: PacketByteBuf get() = PacketByteBufs.empty()

	fun onDisconnect(handler: ServerPlayNetworkHandler, server: MinecraftServer) {
		OnlinePlayerDatabase.handleDisconnection(handler.player.gameProfile)
	}

	fun loginFailed(responseSender: PacketSender, playerEntry: OnlinePlayerDatabase.OnlinePlayer, message: String) {
		responseSender.sendPacket(
			LoginPacketIDs.loginFailed,
			PacketByteBufs
				.create()
				.writeNbt(NbtCompound().also {
					it.putString("Message", message)
				})
		)

		playerEntry.loginInfo.state = LoginInfo.State.HANDSHAKE
	}

	fun onHandshake(
		server: MinecraftServer,
		player: ServerPlayerEntity,
		handler: ServerPlayNetworkHandler,
		buf: PacketByteBuf,
		responseSender: PacketSender
	) {
		val playerEntry = OnlinePlayerDatabase[player.gameProfile]
		if (playerEntry == null) {
			handler.disconnect(Text.of("Missing Online Player Info"))
			return
		}

		if (!playerEntry.loginInfo.state.canHandshake) {
			handler.disconnect(Text.of("Incorrect state: ${playerEntry.loginInfo.state.name}"))
			return
		}

		if (playerEntry.isLoggedIn) {
			handler.disconnect(Text.of("Player already logged in!"))
			throw IllegalStateException("Player ${player.name} (@${playerEntry.currentAccountUUID}, ${playerEntry.currentAccount!!.username}) already logged in!")
		}

		playerEntry.loginInfo.state = LoginInfo.State.HANDSHAKE

		responseSender.sendPacket(LoginPacketIDs.handshake, emptyBuffer)
	}

	fun onRegisterReady(
		server: MinecraftServer,
		player: ServerPlayerEntity,
		handler: ServerPlayNetworkHandler,
		buf: PacketByteBuf,
		responseSender: PacketSender
	) {
		val nbt = buf.readNbt()!!

		val playerEntry = OnlinePlayerDatabase[player.gameProfile]
		if (playerEntry == null) {
			handler.disconnect(Text.of("Missing Online Player Info"))
			return
		}

		if (!playerEntry.loginInfo.state.canRegister) {
			handler.disconnect(Text.of("Incorrect state: ${playerEntry.loginInfo.state.name}"))
			return
		}

		if (playerEntry.isLoggedIn) {
			handler.disconnect(Text.of("Player already logged in!"))
			throw IllegalStateException("Player ${player.name} (@${playerEntry.currentAccountUUID}, ${playerEntry.currentAccount!!.username}) already logged in!")
		}

		playerEntry.loginInfo.requestedUsername = nbt.getString("Username")

		if (playerEntry.loginInfo.requestedUsername == null) {
			handler.disconnect(Text.of("Username is required!"))
			return
		}

		if (!AccountDatabase.canCreateAccount(playerEntry.loginInfo.requestedUsername!!)) {
			loginFailed(responseSender, playerEntry, "Username is not available!")
			return
		}

		playerEntry.loginInfo.state = LoginInfo.State.REGISTER

		responseSender.sendPacket(LoginPacketIDs.registrationReady, emptyBuffer)
	}

	fun onRegister(
		server: MinecraftServer,
		player: ServerPlayerEntity,
		handler: ServerPlayNetworkHandler,
		buf: PacketByteBuf,
		responseSender: PacketSender
	) {
		val nbt = buf.readNbt()!!

		val playerEntry = OnlinePlayerDatabase[player.gameProfile]
		if (playerEntry == null) {
			handler.disconnect(Text.of("Missing Online Player Info"))
			return
		}

		if (!playerEntry.loginInfo.state.canRegisterHash) {
			handler.disconnect(Text.of("Incorrect state: ${playerEntry.loginInfo.state.name}"))
			return
		}

		if (playerEntry.isLoggedIn) {
			handler.disconnect(Text.of("Player already logged in!"))
			throw IllegalStateException("Player ${player.name} (@${playerEntry.currentAccountUUID}, ${playerEntry.currentAccount!!.username}) already logged in!")
		}

		val username = nbt.getString("Username")
		val hash = nbt.getByteArray("Hash")
		val salt = nbt.getByteArray("Salt")

		if (username == null) {
			handler.disconnect(Text.of("Username is required!"))
			return
		}

		if (playerEntry.loginInfo.requestedUsername != username) {
			handler.disconnect(Text.of("Internal error: Usernames do not match!"))
			return
		}

		if (hash.isEmpty()) {
			handler.disconnect(Text.of("Hash is required!"))
			return
		}

		if (salt.isEmpty()) {
			handler.disconnect(Text.of("Salt is required"))
			return
		}

		if (!AccountDatabase.createAccount(
			username,
			AuthInfo(hash, salt),
			if (playerEntry.premiumInfo.premium)
				mutableListOf(playerEntry.premiumInfo.onlineProfile!!) else mutableListOf()
		)) {
			handler.disconnect(Text.of("Account creation failed!"))
			return
		}

		AccountDatabase[username]?.currentPlayerUUID = OnlinePlayerDatabase.getUUID(playerEntry.profile)
		playerEntry.currentAccountUUID = AccountDatabase.getUUID(username)

		playerEntry.loginInfo.state = LoginInfo.State.FINISHED

		responseSender.sendPacket(LoginPacketIDs.showMenu, emptyBuffer)
	}

	fun onLoginReady(
		server: MinecraftServer,
		player: ServerPlayerEntity,
		handler: ServerPlayNetworkHandler,
		buf: PacketByteBuf,
		responseSender: PacketSender
	) {
		val nbt = buf.readNbt()!!

		val playerEntry = OnlinePlayerDatabase[player.gameProfile]
		if (playerEntry == null) {
			handler.disconnect(Text.of("Missing Online Player Info"))
			return
		}

		if (!playerEntry.loginInfo.state.canSendUsername) {
			handler.disconnect(Text.of("Incorrect state: ${playerEntry.loginInfo.state.name}"))
			return
		}

		if (playerEntry.isLoggedIn) {
			handler.disconnect(Text.of("Player already logged in!"))
			throw IllegalStateException("Player ${player.name} (@${playerEntry.currentAccountUUID}, ${playerEntry.currentAccount!!.username}) already logged in!")
		}

		playerEntry.loginInfo.requestedUsername = nbt.getString("Username")

		if (playerEntry.loginInfo.requestedUsername == null) {
			handler.disconnect(Text.of("Username is required!"))
			return
		}

		val account = AccountDatabase[playerEntry.loginInfo.requestedUsername!!]

		if (account == null) {
			loginFailed(responseSender, playerEntry, "Account not found!")
			return
		}

		playerEntry.loginInfo.state = LoginInfo.State.SALT
		playerEntry.loginInfo.nonce = ByteArray(512)
		SecureRandom.getInstanceStrong().nextBytes(playerEntry.loginInfo.nonce)

		responseSender.sendPacket(
			LoginPacketIDs.loginReady,
			PacketByteBufs
				.create()
				.writeNbt(NbtCompound().also {
					it.putByteArray("Nonce", playerEntry.loginInfo.nonce)
					it.putByteArray("Salt", account.authInfo.salt)
				})
		)
	}

	fun onLogin(
		server: MinecraftServer,
		player: ServerPlayerEntity,
		handler: ServerPlayNetworkHandler,
		buf: PacketByteBuf,
		responseSender: PacketSender
	) {
		val nbt = buf.readNbt()!!

		val playerEntry = OnlinePlayerDatabase[player.gameProfile]
		if (playerEntry == null) {
			handler.disconnect(Text.of("Missing Online Player Info"))
			return
		}

		if (!playerEntry.loginInfo.state.canSendHashedPassword) {
			handler.disconnect(Text.of("Incorrect state: ${playerEntry.loginInfo.state.name}"))
			return
		}

		if (playerEntry.isLoggedIn) {
			handler.disconnect(Text.of("Player already logged in!"))
			throw IllegalStateException("Player ${player.name} (@${playerEntry.currentAccountUUID}, ${playerEntry.currentAccount!!.username}) already logged in!")
		}

		val account = AccountDatabase[playerEntry.loginInfo.requestedUsername!!]

		if (account == null) {
			loginFailed(responseSender, playerEntry, "Account not found!")
			return
		}

		val hash = nbt.getByteArray("Hash")

		if (hash.isEmpty()) {
			handler.disconnect(Text.of("Hash is required"))
			return
		}

		val nonce = playerEntry.loginInfo.nonce

		if (!account.authInfo.checkReceivedHash(hash, nonce)) {
			loginFailed(responseSender, playerEntry, "Incorrect password!")
			return
		}

		account.currentPlayerUUID = OnlinePlayerDatabase.getUUID(playerEntry.profile)
		playerEntry.currentAccountUUID = AccountDatabase.getUUID(playerEntry.loginInfo.requestedUsername!!)

		playerEntry.loginInfo.state = LoginInfo.State.FINISHED

		responseSender.sendPacket(LoginPacketIDs.showMenu, emptyBuffer)
	}
}