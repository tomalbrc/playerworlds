package playerworlds.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import playerworlds.logic.Land;
import playerworlds.util.PlayerworldsLevels;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	private World world;

	@Shadow
	public abstract World getWorld();

	@ModifyVariable(method = "tickPortalTeleportation", at = @At(value = "STORE"), ordinal = 0)
	public ServerWorld tickPortal_modifyRegistryKey(ServerWorld serverLevel) {
		// TODO: this part might be removed as well, since the player wont have any portals anyway (at leats not working)

		if (PlayerworldsLevels.isLand(world) && PlayerworldsLevels.isNether(world.getRegistryKey())) {
			Optional<Land> land = PlayerworldsLevels.getLand(world);
			if (land.isPresent()) {
				return this.getWorld().getServer().getWorld(land.get().getWorld().getRegistryKey());
			}
		}

		return serverLevel;
	}
}
