package io.github.ahaququq.wisienka.server

import io.github.ahaququq.wisienka.Wisienka
import io.github.ahaququq.wisienka.Wisienka.Companion.NAME
import net.fabricmc.api.DedicatedServerModInitializer

class WisienkaDedicatedServer : DedicatedServerModInitializer {
	override fun onInitializeServer() {
		Wisienka.logger.info("$NAME is running on a dedicated server")
	}
}