package io.github.ahaququq.wisienka.login

import com.mojang.authlib.GameProfile

data class PremiumInfo(val onlineProfile: GameProfile? = null) {
	val premium: Boolean get() = onlineProfile != null
}
