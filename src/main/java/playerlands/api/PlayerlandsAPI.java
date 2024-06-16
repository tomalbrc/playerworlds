package playerlands.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import playerlands.PlayerlandsMod;
import playerlands.logic.Land;
import playerlands.logic.Playerlands;

import java.util.Optional;
import java.util.UUID;

public class PlayerlandsAPI {

	public static final Event<HubVisitEvent> ON_HUB_VISIT = EventFactory.createArrayBacked(HubVisitEvent.class, callbacks -> (player, world) -> {
		for (HubVisitEvent callback : callbacks) {
			callback.invoke(player, world);
		}
	});
	@FunctionalInterface public interface HubVisitEvent {void invoke(PlayerEntity player, World world);}

	public static final Event<GenericIslandEvent> ON_LAND_VISIT = EventFactory.createArrayBacked(GenericIslandEvent.class, callbacks -> (player, world, land) -> {
		for (GenericIslandEvent callback : callbacks) {
			callback.invoke(player, world, land);
		}
	});

	public static final Event<GenericIslandEvent> ON_LAND_FIRST_LOAD = EventFactory.createArrayBacked(GenericIslandEvent.class, callbacks -> (player, world, land) -> {
		for (GenericIslandEvent callback : callbacks) {
			callback.invoke(player, world, land);
		}
	});
	@FunctionalInterface public interface GenericIslandEvent {void invoke(PlayerEntity player, World world, Land land);}

	public static Optional<Land> getLand(PlayerEntity player) {
		return Playerlands.getIslands().get(player);
	}

	public static Optional<Land> getLand(String playerName) {
		return Playerlands.getIslands().get(playerName);
	}

	public static Optional<Land> getLand(UUID playerUuid) {
		return Playerlands.getIslands().get(playerUuid);
	}

	public static boolean isIsland(World world) {
		return isIsland(world.getRegistryKey());
	}

	public static boolean isIsland(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals(PlayerlandsMod.MOD_ID) || namespace.equals("nether") || namespace.equals("end");
	}

	public static Optional<Land> getLand(World world) {
		if (isIsland(world)) {
			try {
				return Playerlands.getIslands().get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
			}
			catch (Exception e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

}
