package playerlands.mixin.world.protection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
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
import playerlands.util.PlayerlandsTexts;
import playerlands.util.WorldProtection;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireMixin extends Block {

	public RedstoneWireMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if(!world.isClient) {
			if(!WorldProtection.canModify(world, player)) {
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.world_protection.redstone"), true);
				cir.setReturnValue(ActionResult.FAIL);
			}
		}
	}
}
