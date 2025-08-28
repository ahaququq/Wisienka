package io.github.ahaququq.imkotlin

import imgui.ImGui
import imgui.ImGuiInputTextCallbackData
import imgui.ImGuiStyle
import imgui.type.ImBoolean
import imgui.type.ImString

object ImKotlin {
	fun centered(condition: ImCondition = ImCondition.Always): ImKotlin {
		val center = ImGui.getMainViewport().center
		ImGui.setNextWindowPos(center.x, center.y, condition.id, 0.5F, 0.5F)
		return this
	}

	val centered: ImKotlin
		get() = centered()

	/**
	 * # Window Creation DSL
	 * @see ImGui.begin
	 * @see ImGui.end
 	 */


	/***/
	fun window(
		title: String = "Untitled Window",
		code: ImKotlin.() -> Unit
	): Boolean {
		var returns = false
		if (ImGui.begin(title)) {
			this.code()
			returns = true
		}
		ImGui.end()
		return returns
	}

	fun window(
		title: String,
		open: ImBoolean,
		code: ImKotlin.() -> Unit
	): Boolean {
		var returns = false
		if (ImGui.begin(title, open)) {
			this.code()
			returns = true
		}
		ImGui.end()
		return returns
	}

	fun window(
		title: String,
		open: ImBoolean,
		imGuiWindowFlags: WindowFlags,
		code: ImKotlin.() -> Unit
	): Boolean {
		var returns = false
		if (ImGui.begin(title, open, imGuiWindowFlags.id)) {
			this.code()
			returns = true
		}
		ImGui.end()
		return returns
	}

	fun window(
		title: String,
		imGuiWindowFlags: WindowFlags,
		code: ImKotlin.() -> Unit
	): Boolean {
		var returns = false
		if (ImGui.begin(title, imGuiWindowFlags.id)) {
			this.code()
			returns = true
		}
		ImGui.end()
		return returns
	}



	/**
	 * Access the Style structure (colors, sizes). Always use PushStyleCol(), PushStyleVar() to modify style mid-frame!
	 */
	val style: ImGuiStyle
		get() = ImGui.getStyle()



	fun pushStyleColor(imColor: ImColor, col: Int) = ImGui.pushStyleColor(imColor.id, col)
	fun pushStyleColor(imColor: ImColor, r: Int, g: Int, b: Int, a: Int) = ImGui.pushStyleColor(imColor.id, r, g, b, a)
	fun pushStyleColor(imColor: ImColor, r: Float, g: Float, b: Float, a: Float) = ImGui.pushStyleColor(imColor.id, r, g, b, a)

	fun popStyleColor() = ImGui.popStyleColor()
	fun popStyleColor(count: Int) = ImGui.popStyleColor(count)

	fun pushStyleVar(styleVar: StyleVar, x: Float) = ImGui.pushStyleVar(styleVar.id, x)
	fun pushStyleVar(styleVar: StyleVar, x: Float, y: Float) = ImGui.pushStyleVar(styleVar.id, x, y)

	fun popStyleVar() = ImGui.popStyleVar()
	fun popStyleVar(count: Int) = ImGui.popStyleVar(count)



	fun text(text: String) = ImGui.text(text)

	fun textWrapped(text: String) = ImGui.textWrapped(text)



	fun button(
		label: String,
		code: ImKotlin.() -> Unit = {}
	) = if (ImGui.button(label)) {
		this.code()
		true
	} else false

	fun button(
		label: String,
		width: Float,
		height: Float,
		code: ImKotlin.() -> Unit = {}
	) = if (ImGui.button(label, width, height)) {
			this.code()
			true
		} else false

	fun separator() = ImGui.separator()

	fun inputText(
		label: String,
		text: ImString
	) = ImGui.inputText(label, text)

	fun inputText(
		label: String,
		text: ImString,
		vararg flags: InputTextFlags
	) = ImGui.inputText(label, text, flags.fold(InputTextFlags.None.id) { acc, flags -> acc or flags.id})

	fun inputText(
		label: String,
		text: ImString,
		vararg flags: InputTextFlags,
		callback: (ImGuiInputTextCallbackData?) -> Unit
	) = ImGui.inputText(
		label, text,
		flags.fold(InputTextFlags.None.id) { acc, flags -> acc or flags.id },
		InputTextCallback(callback)
	)

	fun checkbox(label: String, active: Boolean) = ImGui.checkbox(label, active)
	fun checkbox(label: String, active: ImBoolean) = ImGui.checkbox(label, active)

	fun sameLine(): ImKotlin {
		ImGui.sameLine()
		return this
	}
	fun sameLine(offsetFromStartX: Float): ImKotlin {
		ImGui.sameLine(offsetFromStartX)
		return this
	}
	fun sameLine(offsetFromStartX: Float, spacing: Float): ImKotlin {
		ImGui.sameLine(offsetFromStartX, spacing)
		return this
	}

	val sameLine: ImKotlin
		get() = sameLine()

	fun progressBar(fraction: Float) = ImGui.progressBar(fraction)
	fun progressBar(fraction: Float, sizeX: Float, sizeY: Float) = ImGui.progressBar(fraction, sizeX, sizeY)

	fun progressBar(fraction: Float, sizeX: Float, sizeY: Float, overlay: String) =
		ImGui.progressBar(fraction, sizeX, sizeY, overlay)

	fun tabBar(
		stringID: String,
		code: ImKotlin.() -> Unit = {}
	) = if (ImGui.beginTabBar(stringID)) {
		code()
		ImGui.endTabBar()
		true
	} else false

	fun tabBar(
		stringID: String,
		flags: Int,
		code: ImKotlin.() -> Unit = {},
	) {
		if (ImGui.beginTabBar(stringID, flags)) {
			code()
			ImGui.endTabBar()
		}
	}

	fun tabItem(
		label: String,
		code: ImKotlin.() -> Unit = {}
	) {
		if (ImGui.beginTabItem(label)) {
			code()
			ImGui.endTabItem()
		}
	}

	fun tabItem(
		label: String,
		pOpen: ImBoolean,
		code: ImKotlin.() -> Unit = {},
	) {
		if (ImGui.beginTabItem(label, pOpen)) {
			code()
			ImGui.endTabItem()
		}
	}

	fun tabItem(
		label: String,
		flags: Int,
		code: ImKotlin.() -> Unit = {},
	) {
		if (ImGui.beginTabItem(label, flags)) {
			code()
			ImGui.endTabItem()
		}
	}

	fun tabItem(
		label: String,
		pOpen: ImBoolean,
		flags: Int,
		code: ImKotlin.() -> Unit = {},
	) {
		if (ImGui.beginTabItem(label, pOpen, flags)) {
			code()
			ImGui.endTabItem()
		}
	}

	fun table(
		id: String,
		column: Int,
		code: ImKotlin.() -> Unit
	) {
		if (ImGui.beginTable(id, column)) {
			code()
			ImGui.endTable()
		}
	}

	fun table(
		id: String,
		column: Int,
		flags: Int,
		code: ImKotlin.() -> Unit,
	) {
		if (ImGui.beginTable(id, column, flags)) {
			code()
			ImGui.endTable()
		}
	}

	fun table(
		id: String,
		column: Int,
		flags: Int,
		outerSizeX: Float,
		outerSizeY: Float,
		code: ImKotlin.() -> Unit,
	) {
		if (ImGui.beginTable(id, column, flags, outerSizeX, outerSizeY)) {
			code()
			ImGui.endTable()
		}
	}

	fun table(
		id: String,
		column: Int,
		flags: Int,
		outerSizeX: Float,
		outerSizeY: Float,
		innerWidth: Float,
		code: ImKotlin.() -> Unit,
	) {
		if (ImGui.beginTable(id, column, flags, outerSizeX, outerSizeY, innerWidth)) {
			code()
			ImGui.endTable()
		}
	}

	fun nextColumn(): ImKotlin {
		ImGui.tableNextColumn()
		return this
	}

	val nextColumn: ImKotlin
		get() = nextColumn()

	fun column(
		code: ImKotlin.() -> Unit
	) {
		ImGui.tableNextColumn()
		code()
	}

	fun tableSetupColumn(label: String) = ImGui.tableSetupColumn(label)
	fun tableSetupColumn(label: String, flags: Int) = ImGui.tableSetupColumn(label, flags)

	fun tableSetupColumn(label: String, flags: Int, intWidthOrWeight: Float) = ImGui.tableSetupColumn(
		label,
		flags,
		intWidthOrWeight
	)

	fun tableSetupColumn(label: String, flags: Int, intWidthOrWeight: Float, userID: Int) = ImGui.tableSetupColumn(
		label,
		flags,
		intWidthOrWeight,
		userID
	)

	fun fullWidth(): ImKotlin {
		ImGui.setNextItemWidth(ImGui.getContentRegionAvailX())
		return this
	}

	val fullWidth: ImKotlin
		get() = fullWidth()

	/**
	 * Runs the given lambda and returns true, to be used with `||` and `&&`
	 */
	fun run(code: ImKotlin.() -> Unit): Boolean {
		this.code()
		return true
	}
}