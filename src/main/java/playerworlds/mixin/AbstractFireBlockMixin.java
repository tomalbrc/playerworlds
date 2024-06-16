package playerworlds.mixin;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playerworlds.util.PlayerworldsLevels;

@Mixin(AbstractFireBlock.class)
public class AbstractFireBlockMixin {
	@Inject(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/NetherPortal;createPortal()V"), cancellable = true)
	private void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
		if (PlayerworldsLevels.isLand(world)) ci.cancel();
	}
}
