package io.github.ahaququq.imkotlin

/**
 * Enumeration for PushStyleColor() / PopStyleColor()
 */
enum class ImColor(val id: Int) {
	Text(0),
	TextDisabled(1),

	/**
	 * Background of normal windows
	 */
	WindowBg(2),

	/**
	 * Background of child windows
	 */
	ChildBg(3),

	/**
	 * Background of popups, menus, tooltips windows
	 */
	PopupBg(4),
	Border(5),
	BorderShadow(6),

	/**
	 * Background of checkbox, radio button, plot, slider, text input
	 */
	FrameBg(7),
	FrameBgHovered(8),
	FrameBgActive(9),
	TitleBg(10),
	TitleBgActive(11),
	TitleBgCollapsed(12),
	MenuBarBg(13),
	ScrollbarBg(14),
	ScrollbarGrab(15),
	ScrollbarGrabHovered(16),
	ScrollbarGrabActive(17),
	CheckMark(18),
	SliderGrab(19),
	SliderGrabActive(20),
	Button(21),
	ButtonHovered(22),
	ButtonActive(23),

	/**
	 * Header* colors are used for CollapsingHeader, TreeNode, Selectable, MenuItem
	 */
	Header(24),
	HeaderHovered(25),
	HeaderActive(26),
	Separator(27),
	SeparatorHovered(28),
	SeparatorActive(29),
	ResizeGrip(30),
	ResizeGripHovered(31),
	ResizeGripActive(32),
	Tab(33),
	TabHovered(34),
	TabActive(35),
	TabUnfocused(36),
	TabUnfocusedActive(37),

	/**
	 * Preview overlay color when about to docking something
	 */
	DockingPreview(38),

	/**
	 * Background color for empty node (e.g. CentralNode with no window docked into it)
	 */
	DockingEmptyBg(39),
	PlotLines(40),
	PlotLinesHovered(41),
	PlotHistogram(42),
	PlotHistogramHovered(43),

	/**
	 * Table header background
	 */
	TableHeaderBg(44),

	/**
	 * Table outer and header borders (prefer using Alpha=1.0 here)
	 */
	TableBorderStrong(45),

	/**
	 * Table inner borders (prefer using Alpha=1.0 here)
	 */
	TableBorderLight(46),

	/**
	 * Table row background (even rows)
	 */
	TableRowBg(47),

	/**
	 * Table row background (odd rows)
	 */
	TableRowBgAlt(48),
	TextSelectedBg(49),
	DragDropTarget(50),

	/**
	 * Gamepad/keyboard: current highlighted item
	 */
	NavHighlight(51),

	/**
	 * Highlight window when using CTRL+TAB
	 */
	NavWindowingHighlight(52),

	/**
	 * Darken/colorize entire screen behind the CTRL+TAB window list, when active
	 */
	NavWindowingDimBg(53),

	/**
	 * Darken/colorize entire screen behind a modal window, when one is active
	 */
	ModalWindowDimBg(54),
	COUNT(55),
}
