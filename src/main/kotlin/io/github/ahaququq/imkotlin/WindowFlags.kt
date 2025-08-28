package io.github.ahaququq.imkotlin

import imgui.flag.ImGuiWindowFlags

/**
 * Flags for ImGui::Begin()
 */
enum class WindowFlags(val id: Int) {
	None(0),
	NoTitleBar(1),

	/**
	 * Disable user resizing with the lower-right grip
	 */
	NoResize(1 shl 1),

	/**
	 * Disable user moving the window
	 */
	NoMove(1 shl 2),

	/**
	 * Disable scrollbars (window can still scroll with mouse or programmatically)
	 */
	NoScrollbar(1 shl 3),

	/**
	 * Disable user vertically scrolling with mouse wheel. On child window, mouse wheel will be forwarded to the parent unless NoScrollbar is also set.
	 */
	NoScrollWithMouse(1 shl 4),

	/**
	 * Disable user collapsing window by double-clicking on it
	 */
	NoCollapse(1 shl 5),

	/**
	 * Resize every window to its content every frame
	 */
	AlwaysAutoResize(1 shl 6),

	/**
	 * Disable drawing background color (WindowBg, etc.) and outside border. Similar as using SetNextWindowBgAlpha(0.0f).
	 */
	NoBackground(1 shl 7),

	/**
	 * Never load/save settings in .ini file
	 */
	NoSavedSettings(1 shl 8),

	/**
	 * Disable catching mouse, hovering test with pass through.
	 */
	NoMouseInputs(1 shl 9),

	/**
	 * Has a menu-bar
	 */
	MenuBar(1 shl 10),

	/**
	 * Allow horizontal scrollbar to appear (off by default). You may use SetNextWindowContentSize(ImVec2(width,0.0f)); prior to calling Begin() to specify width. Read code in imgui_demo in the "Horizontal Scrolling" section.
	 */
	HorizontalScrollbar(1 shl 11),

	/**
	 * Disable taking focus when transitioning from hidden to visible state
	 */
	NoFocusOnAppearing(1 shl 12),

	/**
	 * Disable bringing window to front when taking focus (e.g. clicking on it or programmatically giving it focus)
	 */
	NoBringToFrontOnFocus(1 shl 13),

	/**
	 * Always show vertical scrollbar (even if ContentSize.y `<` Size.y)
	 */
	AlwaysVerticalScrollbar(1 shl 14),

	/**
	 * Always show horizontal scrollbar (even if ContentSize.x `<` Size.x)
	 */
	AlwaysHorizontalScrollbar(1 shl 15),

	/**
	 * Ensure child windows without border uses style.WindowPadding (ignored by default for non-bordered child windows, because more convenient)
	 */
	AlwaysUseWindowPadding(1 shl 16),

	/**
	 * No gamepad/keyboard navigation within the window
	 */
	NoNavInputs(1 shl 17),

	/**
	 * No focusing toward this window with gamepad/keyboard navigation (e.g. skipped by CTRL+TAB)
	 */
	NoNavFocus(1 shl 18),

	/**
	 * Append '*' to title without affecting the ID, as a convenience to avoid using the ### operator. When used in a tab/docking context, tab is selected on closure and closure is deferred by one frame to allow code to cancel the closure (with a confirmation popup, etc.) without flicker.
	 */
	UnsavedDocument(1 shl 20),

	/**
	 * Disable docking of this window
	 */
	NoDocking(1 shl 21),
	NoNav(ImGuiWindowFlags.NoNavInputs or ImGuiWindowFlags.NoNavFocus),
	NoDecoration((
		ImGuiWindowFlags.NoTitleBar or
		ImGuiWindowFlags.NoResize or
		ImGuiWindowFlags.NoScrollbar or
		ImGuiWindowFlags.NoCollapse
	)),
	NoInputs((
		ImGuiWindowFlags.NoMouseInputs or
		ImGuiWindowFlags.NoNavInputs or
		ImGuiWindowFlags.NoNavFocus));
}