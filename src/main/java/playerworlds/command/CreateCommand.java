package playerworlds.command;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerworlds.logic.Land;
import playerworlds.logic.LandStuck;
import playerworlds.logic.Playerworlds;
import playerworlds.util.PlayerworldsTexts;

import static net.minecraft.server.command.CommandManager.literal;

public class CreateCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("world").then(literal("create").requires(Permissions.require("playerworlds.create", true)).executes(context -> {
			var source = context.getSource();
			var player = source.getPlayer();
			if(player != null) {
				CreateCommand.run(player);
			}
			return 1;
		})));
	}

	static void run(ServerPlayerEntity player) {
		LandStuck lands = Playerworlds.instance.worlds;

		if(lands.get(player).isPresent()) {
			player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_create.fail"));
		}
		else {
			Land land = lands.create(player);
			if (Playerworlds.config.teleportAfterIslandCreation) {
				land.visitAsMember(player);
			}
			player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_create.success"));
		}
	}
}
