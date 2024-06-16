package playerworlds.mixin.world.protection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import playerworlds.util.PlayerworldsTexts;
import playerworlds.util.WorldProtection;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		World world = ((Entity)((Object)(this))).getWorld();
		if(!world.isClient && source.getAttacker() instanceof PlayerEntity attacker) {
			boolean outOfWorld = source.equals(world.getDamageSources().outOfWorld());
			if(!WorldProtection.canModify(world, attacker)) {
				attacker.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_protection.entity_hurt"), true);
				cir.setReturnValue(false);
			}
		}
	}
}
