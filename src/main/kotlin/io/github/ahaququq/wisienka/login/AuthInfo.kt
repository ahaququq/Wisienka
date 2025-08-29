package io.github.ahaququq.wisienka.login

import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Version
import io.github.ahaququq.wisienka.login.AuthInfo.Companion.hashWithNonce

private const val i = 1

/**
 * Authentication info - stores the hashed password and the used salt
 * @param hash Hashed password
 * @param salt Used salt
 */
data class AuthInfo(
	var hash: ByteArray,
	var salt: ByteArray
) {
	/**
	 * Checks the received hash
	 * @param received `hashWithNonce(password, salt, nonce)`
	 * @param nonce nonce used by the client
	 * @see hashWithNonce
	 */
	fun checkReceivedHash(received: ByteArray, nonce: ByteArray) = received allEqual hashFast(hash, nonce)

	/**
	 * Hashes the `password` with a generated hash
	 * @param password password to be stored
	 */
	constructor(password: ByteArray) : this(ByteArray(0), ByteArray(512)) {
		val argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id, 512, 512)
		salt = argon2.generateSalt(512)
		hash = hashStrong(password, salt)
	}

	companion object {
		infix fun ByteArray.allEqual(other: ByteArray) =
			if (other.size != size) false else zip(other) { a, b -> a == b }.all { it }

		fun hashStrong(password: ByteArray, salt: ByteArray): ByteArray {
			val argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id, 512, 512)
			return argon2.hashAdvanced(
				16,
				65536,
				1,
				password,
				salt,
				512,
				Argon2Version.DEFAULT_VERSION
			).raw
		}

		fun hashFast(password: ByteArray, salt: ByteArray): ByteArray {
			val argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id, 512, 512)
			return argon2.hashAdvanced(
				1,
				4096,
				1,
				password,
				salt,
				512,
				Argon2Version.DEFAULT_VERSION
			).raw
		}

		fun hashWithNonce(password: ByteArray, salt: ByteArray, nonce: ByteArray): ByteArray {
			val argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id, 512, 512)

			return hashFast(hashStrong(password, salt), nonce)
		}
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as AuthInfo

		if (!hash.contentEquals(other.hash)) return false
		if (!salt.contentEquals(other.salt)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = hash.contentHashCode()
		result = 31 * result + salt.contentHashCode()
		return result
	}
}
