package io.github.ahaququq.wisienka.server.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import io.github.ahaququq.wisienka.Wisienka;
import io.github.ahaququq.wisienka.login.LoginManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class JoinToLoginWorldMixin {
	@Inject(
			method = "onPlayerConnect",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/registry/RegistryKey;",
					opcode = Opcodes.GETSTATIC,
					ordinal = 1,
					shift = At.Shift.BY,
					by = 4
			)
	)
	private void changeDefaultWorld(
			ClientConnection connection,
			ServerPlayerEntity player,
			CallbackInfo ci,
			@Local( name = "registryKey" ) LocalRef<RegistryKey<World>> registryKey,
			@Local( name = "nbtCompound" ) NbtCompound nbtCompound
	) {
		GameProfile profile = player.getGameProfile();
		if (LoginManager.INSTANCE.shouldChangeSpawn(profile)) {
			registryKey.set(RegistryKey.of(RegistryKeys.WORLD, Wisienka.Companion.id("login_world")));
			Wisienka.Companion.getLogger().info("Spawning player {} changed", profile.getName());
		} else {
			Wisienka.Companion.getLogger().info("Spawning player {} not changed", profile.getName());
		}
	}

	@Inject(
			method = "onPlayerConnect",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerPlayerEntity;setServerWorld(Lnet/minecraft/server/world/ServerWorld;)V"
			)
	)
	private void logDefaultWorld(
			ClientConnection connection,
			ServerPlayerEntity player,
			CallbackInfo ci,
			@Local( name = "serverWorld" )  ServerWorld serverWorld,
			@Local( name = "serverWorld2" ) ServerWorld serverWorld2
	) {
		Wisienka.Companion.getLogger().info("Spawning player in dimension1: {}", serverWorld.getDimensionKey());
		Wisienka.Companion.getLogger().info("Spawning player in dimension2: {}", serverWorld.getDimensionKey());
	}
}
