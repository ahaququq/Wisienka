package io.github.ahaququq.wisienka

import com.simibubi.create.Create
import dev.architectury.utils.Env
import dev.architectury.utils.EnvExecutor
import io.github.ahaququq.wisienka.login.LoginManager
import io.github.ahaququq.wisienka.login.ServerLoginHandler
import io.github.ahaququq.wisienka.networking.ServerNetworking
import io.github.ahaququq.wisienka.server.WisienkaServer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Wisienka : ModInitializer {
	companion object {
		const val ID: String = "wisienka"
		const val NAME: String = "Wisienka"
		val logger: Logger = LoggerFactory.getLogger(NAME)

		fun id(path: String?): Identifier {
			return Identifier(ID, path)
		}
	}

	override fun onInitialize() {
		logger.info("$NAME is loading with Create ${Create.VERSION}")
		EnvExecutor.runInEnv(Env.CLIENT) { Runnable {
			logger.info("$NAME is running on the client")
		} }
		EnvExecutor.runInEnv(Env.SERVER) { Runnable {
			logger.info("$NAME is running on the server")
			WisienkaServer.onInitialize()
		} }

		ServerTickEvents.START_SERVER_TICK.register {
			for (player in it.playerManager.playerList) if (
				player.world.dimensionKey.value == id("login_world_type") &&
				LoginManager.shouldChangeSpawn(player.gameProfile)
			) player.setSpawnPoint(
				RegistryKey.of(RegistryKeys.WORLD, id("login_world")),
				BlockPos(0, 65, 0),
				0.0f,
				true,
				false
			)
		}

		ServerNetworking.init()
		ServerLoginHandler.init()
	}
}
