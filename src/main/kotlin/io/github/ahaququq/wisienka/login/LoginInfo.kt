package io.github.ahaququq.wisienka.login

/**
 * Information related to the login procedure
 * @param state The state in which the player is
 * @param nonce Current nonce. Should be reset on each login attempt
 * @param requestedUsername Username from registrationReady packet or loginReady packet
 */
data class LoginInfo(
	var state: State,
	var nonce: ByteArray = ByteArray(512),
	var requestedUsername: String? = null
) {
	enum class State(
		val canHandshake: Boolean,
		val canRegister: Boolean,
		val canRegisterHash: Boolean,
		val canSendUsername: Boolean,
		val canSendHashedPassword: Boolean,
		val shouldRelocate: Boolean
	) {
		/** Waiting for handshake */
		NEW			(true,  false, false, false, false, true ),
		/** Received handshake, waiting for further login / registration */
		HANDSHAKE	(false, true,  false, true,  false, true ),
		/** User decided to register, waiting for the required data */
		REGISTER	(false, false, true,  true,  false, true ),
		/** User decided to log in, sent their username, received their salt and new nonce, waiting for hashed password */
		SALT		(false, false, false, false, true,  true ),
		/** Received hashed password / registered, user is logged in, `OnlinePlayerDatabase` should be updated by this point */
		FINISHED	(false, false, false, false, false, false),
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as LoginInfo

		if (state != other.state) return false
		if (!nonce.contentEquals(other.nonce)) return false
		if (requestedUsername != other.requestedUsername) return false

		return true
	}

	override fun hashCode(): Int {
		var result = state.hashCode()
		result = 31 * result + nonce.contentHashCode()
		result = 31 * result + (requestedUsername?.hashCode() ?: 0)
		return result
	}
}
