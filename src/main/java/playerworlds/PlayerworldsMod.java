package playerworlds;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import playerworlds.command.PlayerworldsCommands;
import playerworlds.config.PlayerworldsConfig;
import playerworlds.event.BlockBreakEvent;
import playerworlds.event.PlayerConnectEvent;
import playerworlds.event.ServerTickEvent;
import playerworlds.logic.Playerworlds;

public class PlayerworldsMod implements ModInitializer {
	public static final String MOD_ID = "playerworlds";
	public static final Logger LOGGER = LoggerFactory.getLogger("playerworlds");

	@Override
	public void onInitialize() {
		PlayerworldsConfig.init();

		ServerLifecycleEvents.SERVER_STARTING.register(server -> Playerworlds.instance = new Playerworlds(server));
		ServerTickEvents.END_SERVER_TICK.register(ServerTickEvent.INSTANCE);
		ServerPlayConnectionEvents.JOIN.register(PlayerConnectEvent.INSTANCE);
		ServerPlayConnectionEvents.DISCONNECT.register(PlayerConnectEvent.INSTANCE);
		PlayerBlockBreakEvents.BEFORE.register(BlockBreakEvent.INSTANCE);

		PlayerworldsCommands.init();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
