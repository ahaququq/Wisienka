package io.github.ahaququq.wisienka.server.mixin;

import io.github.ahaququq.wisienka.Wisienka;
import io.github.ahaququq.wisienka.server.login.LoginManager;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.network.ServerLoginNetworkHandler$1")
public class ServerLoginNetworkHandlerInnerMixin {
	@Shadow
	@Final
	ServerLoginNetworkHandler field_14176;

	@Inject(
			method = "run",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;profile:Lcom/mojang/authlib/GameProfile;",
					ordinal = 2
			)
	)
	private void runMixin(CallbackInfo ci) {
		Wisienka.Companion.getLogger().info("Player login state: {}", field_14176.state.name());
		if (field_14176.profile == null) {
			Wisienka.Companion.getLogger().info("Player does not have a profile");
			field_14176.profile = LoginManager.INSTANCE.newPlayer(null, false);
		} else {
			Wisienka.Companion.getLogger().info("Player old profile: {}", field_14176.profile);
			field_14176.profile = LoginManager.INSTANCE.newPlayer(field_14176.profile, true);
		}
		Wisienka.Companion.getLogger().info("Player new profile: {}", field_14176.profile);
	}
}