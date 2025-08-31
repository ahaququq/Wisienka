package io.github.ahaququq.wisienka.client.screen

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

typealias RenderFunction = () -> ImGuiScreenManager.Result
typealias AfterRenderFunction = () -> ImGuiScreenManager.Result

@Environment(EnvType.CLIENT)
object ImGuiScreenManager {
	enum class Result {
		NONE,
		SKIP_OTHERS,
		CLOSE_SCREEN,
		RESIGN
	}

	private val renderFunctions = mutableListOf<RenderFunction>()
	private val afterRenderFunctions = mutableListOf<AfterRenderFunction>()

	fun registerRender(function: RenderFunction) {
		renderFunctions.add(function)
	}

	fun registerAfterRender(function: AfterRenderFunction) {
		afterRenderFunctions.add(function)
	}

	fun reset() {
		renderFunctions.clear()
		afterRenderFunctions.clear()
	}

	fun render(): Boolean {
		val resigning = mutableListOf<RenderFunction>()

		for (function in renderFunctions) {
			when (function()) {
				Result.SKIP_OTHERS -> break
				Result.CLOSE_SCREEN -> return false
				Result.RESIGN -> resigning.add(function)
				else -> {}
			}
		}

		renderFunctions.removeAll(resigning)

		return true
	}

	fun afterRender(): Boolean {
		val resigning = mutableListOf<AfterRenderFunction>()

		for (function in afterRenderFunctions) {
			when (function()) {
				Result.SKIP_OTHERS -> break
				Result.CLOSE_SCREEN -> return false
				Result.RESIGN -> resigning.add(function)
				else -> {}
			}
		}

		afterRenderFunctions.removeAll(resigning)

		return true
	}
}