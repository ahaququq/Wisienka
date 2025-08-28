package io.github.ahaququq.wisienka.client

import io.github.ahaququq.wisienka.networking.ClientNetworking
import net.fabricmc.api.ClientModInitializer

class WisienkaClient : ClientModInitializer {

	override fun onInitializeClient() {
		ClientNetworking.init()
	}
}
