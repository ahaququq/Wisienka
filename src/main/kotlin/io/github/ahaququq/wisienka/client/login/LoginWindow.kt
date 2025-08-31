package io.github.ahaququq.wisienka.client.login

import imgui.ImGui
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTableFlags
import imgui.type.ImString
import io.github.ahaququq.imkotlin.ImCondition
import io.github.ahaququq.imkotlin.ImKotlin
import io.github.ahaququq.imkotlin.InputTextFlags
import io.github.ahaququq.imkotlin.WindowFlags
import io.github.ahaququq.wisienka.Wisienka
import io.github.ahaququq.wisienka.client.screen.ImGuiScreenManager
import io.github.ahaququq.wisienka.login.AuthInfo.Companion.allEqual
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
object LoginWindow {
	private var loginFailedMessage: String? = null
	private val username = ImString()
	private val password = ImString()
	private val password2 = ImString()
	private var passwordsDontMatch = false

	fun loginWindow(): ImGuiScreenManager.Result {
		var result = ImGuiScreenManager.Result.NONE

		ImGui.setNextWindowPos(ImGui.getMainViewport().centerX, ImGui.getMainViewport().centerY, ImCondition.Always.id, 0.5f, 0.5f)

		ImKotlin.window("Wisienka Login Service", WindowFlags.AlwaysAutoResize) {
			tabBar("MainOptions") {
				result = login(result)

				result = register(result)
			}

			result = popup(result)
		}

		return result
	}

	fun ImKotlin.popup(result: ImGuiScreenManager.Result): ImGuiScreenManager.Result {
		var result1 = result

		if (passwordsDontMatch) ImGui.openPopup("Error Message")
		if (loginFailedMessage != null) ImGui.openPopup("Error Message")

		if (ImGui.beginPopupModal("Error Message", WindowFlags.AlwaysAutoResize.id)) {
			if (passwordsDontMatch) text("Passwords do not match!")
			if (passwordsDontMatch && loginFailedMessage != null) separator()
			if (loginFailedMessage != null) text("Login failed: $loginFailedMessage")

			button("Close") {
				passwordsDontMatch = false
				loginFailedMessage = null
				ImGui.closeCurrentPopup()
			}

			ImGui.endPopup()
		}

		return result1
	}

	private fun ImKotlin.login(result: ImGuiScreenManager.Result): ImGuiScreenManager.Result {
		var result1 = result
		tabItem("Login") {
			textWrapped("Please enter your username and password to login to your account.")
			separator()
			table("UsernamePassword", 2, ImGuiTableFlags.SizingFixedFit) {
				tableSetupColumn("0", ImGuiTableColumnFlags.WidthFixed)
				tableSetupColumn("1", ImGuiTableColumnFlags.WidthStretch)
				column {
					text("Username:")
				}
				column {
					ImGui.setNextItemWidth(ImGui.getContentRegionAvailX())
					inputText("##Username", username)
				}
				column {
					text("Password:")
				}
				column {
					ImGui.setNextItemWidth(ImGui.getContentRegionAvailX())
					if (inputText("##password", password, InputTextFlags.Password, InputTextFlags.EnterReturnsTrue)) {
						passwordLogin()
					}
				}
			}
			table("AlignRight", 3) {
				tableSetupColumn("0", ImGuiTableColumnFlags.WidthStretch)
				tableSetupColumn("1", ImGuiTableColumnFlags.WidthFixed)
				tableSetupColumn("2", ImGuiTableColumnFlags.WidthFixed)
				column { }
				column {
					button("Cancel") {
						Wisienka.Companion.logger.info("Canceled!")
						result1 = ImGuiScreenManager.Result.CLOSE_SCREEN
					}
				}
				column {
					button("Login") {
						passwordLogin()
					}
				}
			}
			separator()
			table("Premium", 2) {
				tableSetupColumn("0", ImGuiTableColumnFlags.WidthStretch)
				tableSetupColumn("1", ImGuiTableColumnFlags.WidthFixed)
				column {
					text("Using a Microsoft account?")
				}
				column {
					button("Premium login") {
						premiumLogin()
					}
				}
			}
		}
		return result1
	}

	private fun ImKotlin.register(result: ImGuiScreenManager.Result): ImGuiScreenManager.Result {
		var result1 = result
		tabItem("Register") {
			textWrapped("Create a new account. Premium accounts also require a password in case of Mojang server failure.")
			separator()
			table("UsernamePassword", 2) {
				tableSetupColumn("0", ImGuiTableColumnFlags.WidthFixed)
				tableSetupColumn("1", ImGuiTableColumnFlags.WidthStretch)
				column {
					text("Username:")
				}
				column {
					fullWidth.inputText("##username", username)
				}
				column {
					text("Password:")
				}
				column {
					fullWidth.inputText("##password1", password, InputTextFlags.Password)
				}
				column {
					text("Repeat password:")
				}
				column {
					if (fullWidth.inputText(
							"##password2",
							password2,
							InputTextFlags.Password,
							InputTextFlags.EnterReturnsTrue
						)
					) {
						register()
					}
				}
			}

			table("AlignRight", 3) {
				tableSetupColumn("0", ImGuiTableColumnFlags.WidthStretch)
				tableSetupColumn("1", ImGuiTableColumnFlags.WidthFixed)
				tableSetupColumn("2", ImGuiTableColumnFlags.WidthFixed)
				column { }
				column {
					button("Cancel") {
						Wisienka.Companion.logger.info("Canceled!")
						result1 = ImGuiScreenManager.Result.CLOSE_SCREEN
					}
				}
				column {
					button("Register") {
						register()
					}
				}
			}
		}
		return result1
	}

	fun passwordLogin() {
		ClientLoginHandler.login(password.data.sliceArray(0..<password.length), username.get())
	}

	fun premiumLogin() {

	}

	fun register() {
		if (!(password.data.sliceArray(0..<password.length) allEqual password2.data.sliceArray(0..<password.length))) {
			passwordsDontMatch = true
			return
		}

		ClientLoginHandler.register(password.data.sliceArray(0..<password.length), username.get())
	}

	fun loginFailed(string: String) {
		loginFailedMessage = string
	}
}