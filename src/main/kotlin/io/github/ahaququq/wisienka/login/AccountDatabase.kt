package io.github.ahaququq.wisienka.login

import com.mojang.authlib.GameProfile
import io.github.ahaququq.wisienka.getAnyValue
import java.util.*

object AccountDatabase {
	val accounts = mutableMapOf<UUID, Account>()

	data class Account(
		val username: String,
		val authInfo: AuthInfo,
		val premiumProfiles: MutableList<GameProfile>,
		var currentPlayerUUID: UUID?,
	) {
		val loggedIn: Boolean get() = currentPlayerUUID == null
		val currentPlayer: OnlinePlayerDatabase.OnlinePlayer? get() = currentPlayerUUID?.let { OnlinePlayerDatabase[it] }

		fun isProfileLinked(profile: GameProfile) = premiumProfiles.contains(profile)
	}

	operator fun get(uuid: UUID): Account? = accounts[uuid]
	operator fun get(username: String): Account? = accounts.filter {
		(_, value) -> value.username == username
	}.getAnyValue()

	/**
	 * Creates a new account
	 * @param username Username
	 * @param authInfo Hashed password with Salt
	 * @param premiumProfiles Premium profile(s) to be linked to the account
	 * @return false if there was already an account with the chosen name or one of the premium profiles
	 */
	fun createAccount(username: String, authInfo: AuthInfo, premiumProfiles: MutableList<GameProfile> = mutableListOf()): Boolean {
		if (accounts.any { (_, account) ->
			account.username == username ||
			account.premiumProfiles.any(fun(profile1: GameProfile): Boolean {
				return premiumProfiles.any(fun(profile2: GameProfile): Boolean {
					return profile2 == profile1
				})
			})
		}) {
			return false
		}

		accounts[UUID.randomUUID()] = Account(
			username,
			authInfo,
			premiumProfiles,
			null
		)

		return true
	}

	/**
	 * Creates a new account
	 * @param username Username
	 * @param authInfo Hashed password with Salt
	 * @param premiumProfiles Premium profile(s) to be linked to the account
	 * @return false if there was already an account with the chosen name or one of the premium profiles
	 */
	fun createAccount(username: String, authInfo: AuthInfo, vararg premiumProfiles: GameProfile) =
		createAccount(username, authInfo, mutableListOf(*premiumProfiles))
}