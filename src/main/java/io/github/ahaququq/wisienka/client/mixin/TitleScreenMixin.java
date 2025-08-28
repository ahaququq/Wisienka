package io.github.ahaququq.wisienka.client.mixin;

import foundry.veil.Veil;
import imgui.ImGui;
import io.github.ahaququq.wisienka.Wisienka;
import io.github.ahaququq.wisienka.client.screen.LoginScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
abstract public class TitleScreenMixin {
	@Unique
	private boolean shouldOpenLoginScreen = false;

	@Inject(
			method = "render",
			at = @At(
					value = "HEAD"
			)
	)
	private void addTestButton(CallbackInfo ci) {
		Veil.withImGui(() -> {
			if (ImGui.beginMainMenuBar()) {
				if (ImGui.menuItem(Text.translatable("wisienka.gui.button.test").getString())) {
					Wisienka.Companion.getLogger().info("Button pressed!");
					shouldOpenLoginScreen = true;
				} else {
					shouldOpenLoginScreen = false;
				}

				ImGui.endMainMenuBar();
			}
		});

		if (shouldOpenLoginScreen) {
			MinecraftClient.getInstance().setScreen(new LoginScreen(((Screen)(Object) this)));
			shouldOpenLoginScreen = false;
		}
	}
}
