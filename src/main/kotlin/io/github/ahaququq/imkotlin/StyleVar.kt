package io.github.ahaququq.imkotlin

/**
 * Enumeration for PushStyleVar() / PopStyleVar() to temporarily modify the ImGuiStyle structure.
 * - The enum only refers to fields of ImGuiStyle which makes sense to be pushed/popped inside UI code.
 * During initialization or between frames, feel free to just poke into ImGuiStyle directly.
 * - Tip: Use your programming IDE navigation facilities on the names in the _second column_ below to find the actual members and their description.
 * In Visual Studio IDE: CTRL+comma ("Edit.NavigateTo") can follow symbols in comments, whereas CTRL+F12 ("Edit.GoToImplementation") cannot.
 * With Visual Assist installed: ALT+G ("VAssistX.GoToImplementation") can also follow symbols in comments.
 * - When changing this enum, you need to update the associated internal table GStyleVarInfo[] accordingly. This is where we link enum values to members offset/type.
 */
enum class StyleVar(val id: Int) {
	/**
	 * float     Alpha
	 */
	Alpha(0),

	/**
	 * float     DisabledAlpha
	 */
	DisabledAlpha(1),

	/**
	 * ImVec2    WindowPadding
	 */
	WindowPadding(2),

	/**
	 * float     WindowRounding
	 */
	WindowRounding(3),

	/**
	 * float     WindowBorderSize
	 */
	WindowBorderSize(4),

	/**
	 * ImVec2    WindowMinSize
	 */
	WindowMinSize(5),

	/**
	 * ImVec2    WindowTitleAlign
	 */
	WindowTitleAlign(6),

	/**
	 * float     ChildRounding
	 */
	ChildRounding(7),

	/**
	 * float     ChildBorderSize
	 */
	ChildBorderSize(8),

	/**
	 * float     PopupRounding
	 */
	PopupRounding(9),

	/**
	 * float     PopupBorderSize
	 */
	PopupBorderSize(10),

	/**
	 * ImVec2    FramePadding
	 */
	FramePadding(11),

	/**
	 * float     FrameRounding
	 */
	FrameRounding(12),

	/**
	 * float     FrameBorderSize
	 */
	FrameBorderSize(13),

	/**
	 * ImVec2    ItemSpacing
	 */
	ItemSpacing(14),

	/**
	 * ImVec2    ItemInnerSpacing
	 */
	ItemInnerSpacing(15),

	/**
	 * float     IndentSpacing
	 */
	IndentSpacing(16),

	/**
	 * ImVec2    CellPadding
	 */
	CellPadding(17),

	/**
	 * float     ScrollbarSize
	 */
	ScrollbarSize(18),

	/**
	 * float     ScrollbarRounding
	 */
	ScrollbarRounding(19),

	/**
	 * float     GrabMinSize
	 */
	GrabMinSize(20),

	/**
	 * float     GrabRounding
	 */
	GrabRounding(21),

	/**
	 * float     TabRounding
	 */
	TabRounding(22),

	/**
	 * ImVec2    ButtonTextAlign
	 */
	ButtonTextAlign(23),

	/**
	 * ImVec2    SelectableTextAlign
	 */
	SelectableTextAlign(24),
	COUNT(25)
}
