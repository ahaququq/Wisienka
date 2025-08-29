package io.github.ahaququq.wisienka.login

import com.mojang.authlib.GameProfile
import io.github.ahaququq.wisienka.getAnyKey
import io.github.ahaququq.wisienka.getAnyValue
import java.util.*

object OnlinePlayerDatabase {
	val players = mutableMapOf<UUID, OnlinePlayer>()

	data class OnlinePlayer(
		val profile: GameProfile,
		val premiumInfo: PremiumInfo,
		val loginInfo: LoginInfo,
		val currentAccountUUID: UUID?,
	) {
		val isLoggedIn: Boolean get() = currentAccountUUID == null
		val currentAccount: AccountDatabase.Account? get() = currentAccountUUID?.let { AccountDatabase[it] }

		/**
		 * Handles the player leaving the server and removes the entry from the database
		 */
		fun handleDisconnection() {
			onDisconnected()
			OnlinePlayerDatabase.onDisconnection(profile)
		}

		/**
		 * Hook for handling the disconnection
		 */
		internal fun onDisconnected() {
			currentAccount?.currentPlayerUUID = null
		}
	}

	operator fun get(uuid: UUID): OnlinePlayer? = players[uuid]
	operator fun get(profile: GameProfile): OnlinePlayer? = players.filter {
			(_, value) -> value.profile == profile
	}.getAnyValue()

	/**
	 * @return A random, temporary `GameProfile`
	 */
	fun temporaryProfile(): GameProfile = UUID.randomUUID().run { GameProfile(this, "%$this".substring(0..15)) }

	/**
	 * Handles a new player logging in to the server
	 * @param premiumInfo Information gathered from the default authentication procedure
	 * @return A temporary profile to be used before the player logs in and chooses one of their own
	 */
	fun handleNewPlayer(premiumInfo: PremiumInfo): GameProfile {
		val temporaryProfile = temporaryProfile()
		players[UUID.randomUUID()] = OnlinePlayer(
			temporaryProfile,
			premiumInfo,
			LoginInfo(LoginInfo.State.NEW),
			null
		)
		return temporaryProfile
	}

	/**
	 * Handles the player leaving the server and removes the entry from the database
	 * @param profile profile of the leaving player
	 */
	fun handleDisconnection(profile: GameProfile) {
		this[profile]?.onDisconnected()
		onDisconnection(profile)
	}

	/**
	 * Removes the player from the server
	 */
	internal fun onDisconnection(profile: GameProfile) {
		players.remove(players.filter { (_, value) -> value.profile == profile }.getAnyKey())
	}
}