package io.github.ahaququq.wisienka.client.mixin;

import io.github.ahaququq.wisienka.Wisienka;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLoginNetworkHandler.class)
public class ForceLoginAuthenticationMixin {
	@Inject(
			method = "joinServerSession",
			at = @At("RETURN"),
			cancellable = true)
	private void ignoreAuthenticationErrors(String serverId, CallbackInfoReturnable<Text> cir) {
		Wisienka.Companion.getLogger().info("Server ID: {}", serverId);
		cir.setReturnValue(null);
	}
}
