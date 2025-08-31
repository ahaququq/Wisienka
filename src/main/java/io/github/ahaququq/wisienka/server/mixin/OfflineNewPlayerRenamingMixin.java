package io.github.ahaququq.wisienka.server.mixin;

import com.mojang.authlib.GameProfile;
import io.github.ahaququq.wisienka.login.OnlinePlayerDatabase;
import io.github.ahaququq.wisienka.login.PremiumInfo;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public class OfflineNewPlayerRenamingMixin {
	@Shadow
	public ServerLoginNetworkHandler.State state;

	@Shadow
	public GameProfile profile;

	@Inject(
			method = "onHello",
			at = @At("TAIL")
	)
	private void offlinePlayer(LoginHelloC2SPacket packet, CallbackInfo ci) {
		if (state == ServerLoginNetworkHandler.State.READY_TO_ACCEPT) {
			profile = OnlinePlayerDatabase.INSTANCE.handleNewPlayer(new PremiumInfo());
		}
	}
}
