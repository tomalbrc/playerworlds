package playerworlds.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import playerworlds.logic.Playerworlds;

public class ServerTickEvent implements ServerTickEvents.EndTick {
	public static final ServerTickEvent INSTANCE = new ServerTickEvent();

	@Override
	public void onEndTick(MinecraftServer server) {
		Playerworlds.getInstance().onTick(server);
	}
}
