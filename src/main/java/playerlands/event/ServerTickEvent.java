package playerlands.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import playerlands.logic.Playerlands;

public class ServerTickEvent implements ServerTickEvents.EndTick {
	public static final ServerTickEvent INSTANCE = new ServerTickEvent();

	@Override
	public void onEndTick(MinecraftServer server) {
		Playerlands.getInstance().onTick(server);
	}
}
