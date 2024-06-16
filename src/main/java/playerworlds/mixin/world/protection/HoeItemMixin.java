package playerworlds.mixin.world.protection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import playerworlds.util.PlayerworldsTexts;
import playerworlds.util.WorldProtection;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin {

	@Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
	void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();
		if(!world.isClient && player != null) {
			if(!WorldProtection.canModify(world, player)) {
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_protection.hoe_use"), true);
				cir.setReturnValue(ActionResult.FAIL);
			}
		}
	}
}
