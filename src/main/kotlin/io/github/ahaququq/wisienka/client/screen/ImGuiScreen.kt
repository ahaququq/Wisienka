package io.github.ahaququq.wisienka.client.screen

import foundry.veil.Veil
import io.github.ahaququq.imkotlin.ImColor
import io.github.ahaququq.imkotlin.ImKotlin
import io.github.ahaququq.imkotlin.StyleVar
import io.github.ahaququq.wisienka.Wisienka
import io.github.ahaququq.wisienka.networking.PacketIDs
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.gui.CubeMapRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.RotatingCubeMapRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class ImGuiScreen(var parent: Screen? = null) : Screen(Text.translatable("wisienka.gui.imgui.title")) {
	val closable: Boolean
		get() = parent != null

	override fun init() {
	}

	override fun close() {
		if (parent != null) client!!.setScreen(parent)
	}

	override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
		backgroundRenderer.render(delta, 1.0f)

		var shouldClose = false

		Veil.withImGui {
			setStyle()

			shouldClose = !ImGuiScreenManager.render()

			resetStyle()
		}

		super.render(context, mouseX, mouseY, delta)

		if (!ImGuiScreenManager.afterRender()) shouldClose = true

		if (shouldClose) {
			if (closable) {
				close()
			} else {
				ClientPlayNetworking.send(PacketIDs.LOGIN_CANCEL_PACKET_C2S, PacketByteBufs.empty())
			}
		}
	}

	fun setStyle() {
		ImKotlin.pushStyleColor(ImColor.Text,                   0.00f, 0.00f, 0.00f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.TextDisabled,           0.60f, 0.60f, 0.60f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.WindowBg,               0.94f, 0.94f, 0.94f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.ChildBg,                0.00f, 0.00f, 0.00f, 0.00f)
		ImKotlin.pushStyleColor(ImColor.PopupBg,                1.00f, 1.00f, 1.00f, 0.98f)
		ImKotlin.pushStyleColor(ImColor.Border,                 0.00f, 0.00f, 0.00f, 0.30f)
		ImKotlin.pushStyleColor(ImColor.BorderShadow,           0.00f, 0.00f, 0.00f, 0.00f)
		ImKotlin.pushStyleColor(ImColor.FrameBg,                1.00f, 1.00f, 1.00f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.FrameBgHovered,         0.26f, 0.59f, 0.98f, 0.40f)
		ImKotlin.pushStyleColor(ImColor.FrameBgActive,          0.26f, 0.59f, 0.98f, 0.67f)
		ImKotlin.pushStyleColor(ImColor.TitleBg,                0.96f, 0.96f, 0.96f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.TitleBgActive,          0.82f, 0.82f, 0.82f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.TitleBgCollapsed,       1.00f, 1.00f, 1.00f, 0.51f)
		ImKotlin.pushStyleColor(ImColor.MenuBarBg,              0.86f, 0.86f, 0.86f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.ScrollbarBg,            0.98f, 0.98f, 0.98f, 0.53f)
		ImKotlin.pushStyleColor(ImColor.ScrollbarGrab,          0.69f, 0.69f, 0.69f, 0.80f)
		ImKotlin.pushStyleColor(ImColor.ScrollbarGrabHovered,   0.49f, 0.49f, 0.49f, 0.80f)
		ImKotlin.pushStyleColor(ImColor.ScrollbarGrabActive,    0.49f, 0.49f, 0.49f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.CheckMark,              0.26f, 0.59f, 0.98f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.SliderGrab,             0.26f, 0.59f, 0.98f, 0.78f)
		ImKotlin.pushStyleColor(ImColor.SliderGrabActive,       0.46f, 0.54f, 0.80f, 0.60f)
		ImKotlin.pushStyleColor(ImColor.Button,                 0.26f, 0.59f, 0.98f, 0.40f)
		ImKotlin.pushStyleColor(ImColor.ButtonHovered,          0.26f, 0.59f, 0.98f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.ButtonActive,           0.06f, 0.53f, 0.98f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.Header,                 0.26f, 0.59f, 0.98f, 0.31f)
		ImKotlin.pushStyleColor(ImColor.HeaderHovered,          0.26f, 0.59f, 0.98f, 0.80f)
		ImKotlin.pushStyleColor(ImColor.HeaderActive,           0.26f, 0.59f, 0.98f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.Separator,              0.39f, 0.39f, 0.39f, 0.62f)
		ImKotlin.pushStyleColor(ImColor.SeparatorHovered,       0.14f, 0.44f, 0.80f, 0.78f)
		ImKotlin.pushStyleColor(ImColor.SeparatorActive,        0.14f, 0.44f, 0.80f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.ResizeGrip,             0.35f, 0.35f, 0.35f, 0.17f)
		ImKotlin.pushStyleColor(ImColor.ResizeGripHovered,      0.26f, 0.59f, 0.98f, 0.67f)
		ImKotlin.pushStyleColor(ImColor.ResizeGripActive,       0.26f, 0.59f, 0.98f, 0.95f)
		ImKotlin.pushStyleColor(ImColor.Tab,                    0.76f, 0.80f, 0.84f, 0.93f)
		ImKotlin.pushStyleColor(ImColor.TabHovered,             0.26f, 0.59f, 0.98f, 0.80f)
		ImKotlin.pushStyleColor(ImColor.TabActive,              0.60f, 0.73f, 0.88f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.TabUnfocused,           0.92f, 0.93f, 0.94f, 0.99f)
		ImKotlin.pushStyleColor(ImColor.TabUnfocusedActive,     0.74f, 0.82f, 0.91f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.DockingPreview,         0.26f, 0.59f, 0.98f, 0.22f)
		ImKotlin.pushStyleColor(ImColor.DockingEmptyBg,         0.20f, 0.20f, 0.20f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.PlotLines,              0.39f, 0.39f, 0.39f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.PlotLinesHovered,       1.00f, 0.43f, 0.35f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.PlotHistogram,          0.90f, 0.70f, 0.00f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.PlotHistogramHovered,   1.00f, 0.45f, 0.00f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.TableHeaderBg,          0.78f, 0.87f, 0.98f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.TableBorderStrong,      0.57f, 0.57f, 0.64f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.TableBorderLight,       0.68f, 0.68f, 0.74f, 1.00f)
		ImKotlin.pushStyleColor(ImColor.TableRowBg,             0.00f, 0.00f, 0.00f, 0.00f)
		ImKotlin.pushStyleColor(ImColor.TableRowBgAlt,          0.30f, 0.30f, 0.30f, 0.09f)
		ImKotlin.pushStyleColor(ImColor.TextSelectedBg,         0.26f, 0.59f, 0.98f, 0.35f)
		ImKotlin.pushStyleColor(ImColor.DragDropTarget,         0.26f, 0.59f, 0.98f, 0.95f)
		ImKotlin.pushStyleColor(ImColor.NavHighlight,           0.26f, 0.59f, 0.98f, 0.80f)
		ImKotlin.pushStyleColor(ImColor.NavWindowingHighlight,  0.70f, 0.70f, 0.70f, 0.70f)
		ImKotlin.pushStyleColor(ImColor.NavWindowingDimBg,      0.20f, 0.20f, 0.20f, 0.20f)
		ImKotlin.pushStyleColor(ImColor.ModalWindowDimBg,       0.20f, 0.20f, 0.20f, 0.35f)

		ImKotlin.pushStyleVar(StyleVar.WindowRounding,		6.0f)
		ImKotlin.pushStyleVar(StyleVar.ChildRounding,		6.0f)
		ImKotlin.pushStyleVar(StyleVar.FrameRounding,		6.0f)
		ImKotlin.pushStyleVar(StyleVar.GrabRounding,		6.0f)
		ImKotlin.pushStyleVar(StyleVar.PopupRounding,		6.0f)
		ImKotlin.pushStyleVar(StyleVar.ScrollbarRounding,	6.0f)
		ImKotlin.pushStyleVar(StyleVar.TabRounding,			6.0f)

		ImKotlin.pushStyleVar(StyleVar.ChildBorderSize,		1.0f)
		ImKotlin.pushStyleVar(StyleVar.FrameBorderSize,		1.0f)
		ImKotlin.pushStyleVar(StyleVar.PopupBorderSize,		1.0f)
		ImKotlin.pushStyleVar(StyleVar.WindowBorderSize,	1.0f)

		ImKotlin.pushStyleVar(StyleVar.WindowTitleAlign, 0.5f, 0.5f)
	}

	fun resetStyle() {
		ImKotlin.popStyleColor(55)
		ImKotlin.popStyleVar(7)
		ImKotlin.popStyleVar(4)
		ImKotlin.popStyleVar()

	}

	companion object {
		val backgroundRenderer = RotatingCubeMapRenderer(CubeMapRenderer(
			Wisienka.id("textures/gui/login/background/panorama/panorama")
		))
	}
}