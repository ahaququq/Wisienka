package io.github.ahaququq.wisienka.server.mixin;

import com.railwayteam.railways.Railways;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Railways.class)
public class RailwaysMixin {
	@Inject(
			method = "init",
			at = @At(
					value = "INVOKE",
					target = "Lorg/spongepowered/asm/mixin/MixinEnvironment;getCurrentEnvironment()Lorg/spongepowered/asm/mixin/MixinEnvironment;"),
			cancellable = true,
			remap = false
	)
	private static void stopAudit(CallbackInfo ci) {
		ci.cancel();
	}
}
