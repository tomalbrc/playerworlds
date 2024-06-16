package playerworlds.mixin.server;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playerworlds.logic.Playerworlds;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

	@Inject(method = "onPlayerConnect", at = @At("TAIL"))
	void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
		if(player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) == 0) {
			Playerworlds.instance.worlds.create(player);
		}
	}

	@ModifyVariable(
			method = "sendWorldInfo",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/world/ServerWorld;getWorldBorder()Lnet/minecraft/world/border/WorldBorder;", shift = At.Shift.AFTER)
	)
	private WorldBorder getPlayerWorldBorder(WorldBorder worldBorder, ServerPlayerEntity player, ServerWorld world) {
		return world.getWorldBorder();
	}
}
