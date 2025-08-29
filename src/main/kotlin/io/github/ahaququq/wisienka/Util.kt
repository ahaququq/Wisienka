package io.github.ahaququq.wisienka

/**
 * @return The first value of the `Map`, or null if empty
 * @see getAnyKey
 * @see getAny
 */
fun <T: Map<K, V>, K, V> T.getAnyValue(): V? {
	return this.values.getAny()
}

/**
 * @return The first key of the `Map`, or null if empty
 * @see getAnyValue
 * @see getAny
 */
fun <T: Map<K, V>, K, V> T.getAnyKey(): K? {
	return this.keys.getAny()
}

/**
 * @return The first value of the `Collection`, or null if empty
 * @see getAnyKey
 * @see getAnyValue
 */
fun <T: Collection<V>, V> T.getAny(): V? {
	return this.let { if (it.isEmpty()) null else it.first() }
}