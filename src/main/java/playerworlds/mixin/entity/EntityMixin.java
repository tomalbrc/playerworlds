package playerworlds.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import playerworlds.logic.Land;
import playerworlds.util.PlayerworldsLevels;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	private World world;

	@ModifyVariable(method = "tickPortal", at = @At("STORE"), ordinal = 0)
	public RegistryKey<World> tickPortal_modifyRegistryKey(RegistryKey<World> instance) {
		// TODO: this part might be removed as well, since the player wont have any portals anyway (at leats not working)
		if (PlayerworldsLevels.isLand(world) && PlayerworldsLevels.isNether(world.getRegistryKey())) {
			Optional<Land> land = PlayerworldsLevels.getLand(world);
			if (land.isPresent()) {
				return land.get().getWorld().getRegistryKey();
			}
		}
		return instance;
	}

	@Redirect(method = "getTeleportTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getRegistryKey()Lnet/minecraft/registry/RegistryKey;"))
	public RegistryKey<World> getTeleportTarget_redirectRegistryKey0(ServerWorld instance) {
		return PlayerworldsLevels.redirect(instance.getRegistryKey());
	}

	@Redirect(method = "getTeleportTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getRegistryKey()Lnet/minecraft/registry/RegistryKey;"))
	public RegistryKey<World> getTeleportTarget_redirectRegistryKey(World instance) {
		return PlayerworldsLevels.redirect(instance.getRegistryKey());
	}


	@Redirect(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getRegistryKey()Lnet/minecraft/registry/RegistryKey;", ordinal = 0))
	public RegistryKey<World> moveToWorld_redirectRegistryKey(ServerWorld instance) {
		return PlayerworldsLevels.redirect(instance.getRegistryKey());
	}
}
