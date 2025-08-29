package io.github.ahaququq.wisienka.login

import io.github.ahaququq.wisienka.Wisienka

object LoginPacketIDs {
	val handshake			= Wisienka.id("login/handshake")
	val registrationReady	= Wisienka.id("login/registration/ready")
	val register			= Wisienka.id("login/registration/data")
	val loginReady			= Wisienka.id("login/login/ready")
	val login				= Wisienka.id("login/login/data")
	val showMenu			= Wisienka.id("menu/show")
}