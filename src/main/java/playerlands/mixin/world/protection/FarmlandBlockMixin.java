package playerlands.mixin.world.protection;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playerlands.util.PlayerlandsTexts;
import playerlands.util.WorldProtection;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {

	@Inject(method = "onLandedUpon", at = @At("HEAD"), cancellable = true)
	void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
		if(!world.isClient && entity instanceof PlayerEntity player) {
			if(!WorldProtection.canModify(world, player)) {
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.world_protection.farmland_spoil"), true);
				ci.cancel();
			}
		}
	}
}
