package playerworlds.mixin.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playerworlds.logic.Playerworlds;
import playerworlds.util.PlayerworldsLevels;
import playerworlds.util.WorldProtection;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

	@Shadow
	public abstract ServerWorld getServerWorld();

	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	void tick(CallbackInfo ci) {
		ServerPlayerEntity player = ServerPlayerEntity.class.cast(this);
		ServerWorld world = getServerWorld();

		if(!WorldProtection.canModify(world, player)) {
			if(player.getPos().getY() < world.getDimension().minY() - 10) {
				PlayerworldsLevels.getLand(world).ifPresentOrElse(land -> {
					var pos = land.visitsPos;
					player.teleport(land.getWorld(), pos.x, pos.y, pos.z, Set.of(), pos.yaw, pos.pitch);
				}, () -> Playerworlds.overworld(player));
			}
		}
	}

	@Redirect(method = "worldChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getRegistryKey()Lnet/minecraft/registry/RegistryKey;"))
	public RegistryKey<World> worldChanged_redirectRegistryKey(ServerWorld instance) {
		return PlayerworldsLevels.redirect(instance.getRegistryKey());
	}
}
