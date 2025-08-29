package io.github.ahaququq.wisienka.networking

import io.github.ahaququq.wisienka.Wisienka

object PacketIDs {
	val LOGIN_HANDSHAKE_PACKET_C2S = Wisienka.id("login/handshake_c2s")
	val LOGIN_HANDSHAKE_PACKET_S2C = Wisienka.id("login/handshake_s2c")
	val LOGIN_CANCEL_PACKET_C2S = Wisienka.id("login/cancel_c2s")

	val LOGIN_READY_PACKET_C2S = Wisienka.id("login/ready_c2s")
	val LOGIN_REGISTER_PACKET_C2S = Wisienka.id("login/register_c2s")

//	val LOGIN_USERNAME_PACKET_C2S = Wisienka.id("login/username_c2s")
	val LOGIN_SALT_PACKET_S2C = Wisienka.id("login/salt_s2c")
	val LOGIN_HASH_PACKET_C2S = Wisienka.id("login/hash_c2s")
	val LOGIN_FINISHED_PACKET_S2C = Wisienka.id("login/finished_s2c")

//	val LOGIN__PACKET_C2S = Wisienka.id("login/_c2s")
//	val LOGIN__PACKET_S2C = Wisienka.id("login/_s2c")
}