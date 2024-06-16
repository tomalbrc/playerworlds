package playerlands;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import playerlands.command.PlayerlandsCommands;
import playerlands.config.PlayerlandsConfig;
import playerlands.event.BlockBreakEvent;
import playerlands.event.PlayerConnectEvent;
import playerlands.event.ServerTickEvent;
import playerlands.logic.Playerlands;

public class PlayerlandsMod implements ModInitializer {
	public static final String MOD_ID = "playerlands";
	public static final Logger LOGGER = LoggerFactory.getLogger("Playerlands");

	@Override
	public void onInitialize() {
		PlayerlandsConfig.init();

		ServerLifecycleEvents.SERVER_STARTING.register(server -> Playerlands.instance = new Playerlands(server));
		ServerTickEvents.END_SERVER_TICK.register(ServerTickEvent.INSTANCE);
		ServerPlayConnectionEvents.JOIN.register(PlayerConnectEvent.INSTANCE);
		ServerPlayConnectionEvents.DISCONNECT.register(PlayerConnectEvent.INSTANCE);
		PlayerBlockBreakEvents.BEFORE.register(BlockBreakEvent.INSTANCE);

		PlayerlandsCommands.init();
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
