package playerworlds.mixin.world.protection;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import playerworlds.util.PlayerworldsTexts;
import playerworlds.util.WorldProtection;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {

	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if(!world.isClient) {
			if(!WorldProtection.canModify(world, player)) {
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_protection.chest_open"), true);
				cir.setReturnValue(ActionResult.FAIL);
			}
		}
	}
}
