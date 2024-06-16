package playerworlds.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class WorldProtection {

	public static boolean canModify(World world, PlayerEntity player) {
		var land = PlayerworldsLevels.getLand(world);

		if(Permissions.check(player, "playerworlds.world.protection.bypass", false)) {
			return true;
		}

		if(land.isPresent() && !land.get().isMember(player)) {
			return false;
		}

		return true;
	}

	public static boolean canModify(PlayerEntity player) {
		return canModify(player.getWorld(), player);
	}
}
