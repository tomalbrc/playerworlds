package playerlands.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import playerlands.PlayerlandsMod;
import playerlands.logic.Land;
import playerlands.logic.Playerlands;

import java.util.Optional;
import java.util.UUID;

public class PlayerlandsLevels {

	public static boolean isLand(World world) {
		return isLand(world.getRegistryKey());
	}

	public static boolean isLand(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals(PlayerlandsMod.MOD_ID);
	}

	public static boolean isOverworld(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals(PlayerlandsMod.MOD_ID) || registryKey == World.OVERWORLD;
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
			return Playerlands.instance.lands.get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
		}
		return Optional.empty();
	}
}
