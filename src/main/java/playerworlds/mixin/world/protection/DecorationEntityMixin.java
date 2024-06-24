package playerworlds.mixin.world.protection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.BlockAttachedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import playerworlds.util.PlayerworldsTexts;
import playerworlds.util.WorldProtection;

@Mixin(BlockAttachedEntity.class)
public abstract class DecorationEntityMixin extends Entity {

	public DecorationEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "handleAttack", at = @At("HEAD"), cancellable = true)
	void damage(Entity attacker, CallbackInfoReturnable<Boolean> cir) {
		World world = getWorld();
		if(!world.isClient && attacker instanceof PlayerEntity player) {
			if(!WorldProtection.canModify(world, player)) {
				attacker.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_protection.entity_hurt"));
				cir.setReturnValue(false);
			}
		}
	}
}
