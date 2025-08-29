package io.github.ahaququq.wisienka.login

import imgui.ImGui
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTableFlags
import imgui.type.ImString
import io.github.ahaququq.imkotlin.ImCondition
import io.github.ahaququq.imkotlin.ImKotlin
import io.github.ahaququq.imkotlin.InputTextFlags
import io.github.ahaququq.imkotlin.WindowFlags
import io.github.ahaququq.wisienka.Wisienka
import io.github.ahaququq.wisienka.client.screen.ImGuiScreenManager.Result
import io.github.ahaququq.wisienka.login.AuthInfo.Companion.allEqual

object LoginWindow {
	val username = ImString()
	val password = ImString()
	val password2 = ImString()
	var passwordsDontMatch = false

	fun loginWindow(): Result {
		var result = Result.NONE

		ImGui.setNextWindowPos(ImGui.getMainViewport().centerX, ImGui.getMainViewport().centerY, ImCondition.Always.id, 0.5f, 0.5f)

		ImKotlin.window("Wisienka Login Service", WindowFlags.AlwaysAutoResize) {
			tabBar("MainOptions") {
				result = login(result)

				result = register(result)
			}
		}

		return result
	}

	private fun ImKotlin.login(result: Result): Result {
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
						Wisienka.logger.info("Canceled!")
						result1 = Result.SKIP_OTHERS
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

	private fun ImKotlin.register(result: Result): Result {
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
						Wisienka.logger.info("Canceled!")
						result1 = Result.SKIP_OTHERS
					}
				}
				column {
					button("Register") {
						register()
					}
				}
			}

			if (passwordsDontMatch) {
				separator()
				table("PasswordsDontMatch", 2) {
					tableSetupColumn("0", ImGuiTableColumnFlags.WidthStretch)
					tableSetupColumn("1", ImGuiTableColumnFlags.WidthFixed)
					column {
						text("Passwords do not match!")
					}
					column {
						button("X") {
							passwordsDontMatch = false
						}
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
}