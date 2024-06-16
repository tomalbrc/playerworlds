package playerworlds.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import playerworlds.PlayerworldsMod;
import playerworlds.logic.Land;
import playerworlds.logic.Playerworlds;

import java.util.Optional;
import java.util.UUID;

public class PlayerworldsLevels {

	public static boolean isLand(World world) {
		return isLand(world.getRegistryKey());
	}

	public static boolean isLand(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals(PlayerworldsMod.MOD_ID);
	}

	public static boolean isOverworld(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals(PlayerworldsMod.MOD_ID) || registryKey == World.OVERWORLD;
	}

	public static boolean isNether(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals("nether") || registryKey == World.NETHER;
	}

	public static boolean isEnd(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals("end") || registryKey == World.END;
	}

	public static RegistryKey<World> redirect(RegistryKey<World> registryKey) {
		if (isOverworld(registryKey)) {
			return World.OVERWORLD;
		}
		if (isEnd(registryKey)) {
			return World.END;
		}
		if (isNether(registryKey)) {
			return World.NETHER;
		}
		return registryKey;
	}

	public static Optional<Land> getLand(World world) {
		if (isLand(world)) {
			return Playerworlds.instance.worlds.get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
		}
		return Optional.empty();
	}
}
