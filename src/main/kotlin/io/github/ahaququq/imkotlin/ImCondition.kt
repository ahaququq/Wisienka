package io.github.ahaququq.imkotlin

/**
 * Enumeration for ImGui::SetWindow***(), SetNextWindow***(), SetNextItem***() functions
 * Represent a condition.
 * Important: Treat as a regular enum! Do NOT combine multiple values using binary operators! All the functions above treat 0 as a shortcut to ImGuiCond_Always.
 */
enum class ImCondition(val id: Int) {
	/**
	 * No condition (always set the variable), same as Always.
	 */
	None(0),

	/**
	 * No condition (always set the variable)
	 */
	Always(1),

	/**
	 * Set the variable once per runtime session (only the first call will succeed)
	 */
	Once(1 shl 1),

	/**
	 * Set the variable if the object/window has no persistently saved data (no entry in .ini file)
	 */
	FirstUseEver(1 shl 2),

	/**
	 * Set the variable if the object/window is appearing after being hidden/inactive (or the first time)
	 */
	Appearing(1 shl 3),
}
