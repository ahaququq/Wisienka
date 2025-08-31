package io.github.ahaququq.wisienka.client

import io.github.ahaququq.wisienka.client.login.ClientLoginHandler
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class WisienkaClient : ClientModInitializer {
	override fun onInitializeClient() {
		ClientLoginHandler.init()
	}
}
