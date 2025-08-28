package io.github.ahaququq.imkotlin

/**
 * Flags for ImGui::InputText()
 */
enum class InputTextFlags(val id: Int) {
	None(0),

	/**
	 * Allow 0123456789.+-* /
	 */
	CharsDecimal(1),

	/**
	 * Allow 0123456789ABCDEFabcdef
	 */
	CharsHexadecimal(1 shl 1),

	/**
	 * Turn a..z into A..Z
	 */
	CharsUppercase(1 shl 2),

	/**
	 * Filter out spaces, tabs
	 */
	CharsNoBlank(1 shl 3),

	/**
	 * Select entire text when first taking mouse focus
	 */
	AutoSelectAll(1 shl 4),

	/**
	 * Return 'true' when Enter is pressed (as opposed to every time the value was modified). Consider looking at the IsItemDeactivatedAfterEdit() function.
	 */
	EnterReturnsTrue(1 shl 5),

	/**
	 * Callback on pressing TAB (for completion handling)
	 */
	CallbackCompletion(1 shl 6),

	/**
	 * Callback on pressing Up/Down arrows (for history handling)
	 */
	CallbackHistory(1 shl 7),

	/**
	 * Callback on each iteration. User code may query cursor position, modify text buffer.
	 */
	CallbackAlways(1 shl 8),

	/**
	 * Callback on character inputs to replace or discard them. Modify 'EventChar' to replace or discard, or return 1 in callback to discard.
	 */
	CallbackCharFilter(1 shl 9),

	/**
	 * Pressing TAB input a '\t' character into the text field
	 */
	AllowTabInput(1 shl 10),

	/**
	 * In multi-line mode, unfocus with Enter, add new line with Ctrl+Enter (default is opposite: unfocus with Ctrl+Enter, add line with Enter).
	 */
	CtrlEnterForNewLine(1 shl 11),

	/**
	 * Disable following the cursor horizontally
	 */
	NoHorizontalScroll(1 shl 12),

	/**
	 * Overwrite mode
	 */
	AlwaysOverwrite(1 shl 13),

	/**
	 * Read-only mode
	 */
	ReadOnly(1 shl 14),

	/**
	 * Password mode, display all characters as '*'
	 */
	Password(1 shl 15),

	/**
	 * Disable undo/redo. Note that input text owns the text data while active, if you want to provide your own undo/redo stack you need e.g. to call ClearActiveID().
	 */
	NoUndoRedo(1 shl 16),

	/**
	 * Allow 0123456789.+-* /eE (Scientific notation input)
	 */
	CharsScientific(1 shl 17),

	/**
	 * Callback on buffer capacity changes request (beyond 'buf_size' parameter value), allowing the string to grow.
	 * Notify when the string wants to be resized (for string types which hold a cache of their Size).
	 * You will be provided a new BufSize in the callback and NEED to honor it. (see misc/cpp/imgui_stdlib.h for an example of using this)
	 */
	CallbackResize(1 shl 18),

	/**
	 * Callback on any edit (note that InputText() already returns true on edit, the callback is useful mainly to manipulate the underlying buffer while focus is active).
	 */
	CallbackEdit(1 shl 19),
}
