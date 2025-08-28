package io.github.ahaququq.imkotlin

import imgui.ImGuiInputTextCallbackData
import imgui.callback.ImGuiInputTextCallback

/**
 * Wrapper for `ImGuiInputTextCallback` to use a lambda function
 * @see ImGuiInputTextCallback
 */
class InputTextCallback(val callback: (ImGuiInputTextCallbackData?) -> Unit) : ImGuiInputTextCallback() {
	override fun accept(t: ImGuiInputTextCallbackData?) {
		callback(t)
	}
}