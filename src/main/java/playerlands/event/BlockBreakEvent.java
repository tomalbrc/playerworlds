package playerlands.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import playerlands.util.PlayerlandsTexts;
import playerlands.util.WorldProtection;

@SuppressWarnings("unused")
public class BlockBreakEvent implements PlayerBlockBreakEvents.Before {
	public static final BlockBreakEvent INSTANCE = new BlockBreakEvent();

	@Override
	public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
		if(!world.isClient() && !WorldProtection.canModify(world, player)) {
			player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.world_protection.block_break"), true);
			return false;
		}
		return true;
	}
}
