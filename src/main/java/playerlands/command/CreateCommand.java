package playerlands.command;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerlands.logic.Land;
import playerlands.logic.LandStuck;
import playerlands.logic.Playerlands;
import playerlands.util.PlayerlandsTexts;

import static net.minecraft.server.command.CommandManager.literal;

public class CreateCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("land").then(literal("create").requires(Permissions.require("playerlands.create", true)).executes(context -> {
			var source = context.getSource();
			var player = source.getPlayer();
			if(player != null) {
				CreateCommand.run(player);
			}
			return 1;
		})));
	}

	static void run(ServerPlayerEntity player) {
		LandStuck lands = Playerlands.instance.lands;

		if(lands.get(player).isPresent()) {
			player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.land_create.fail"));
		}
		else {
			Land land = lands.create(player);
			if(Playerlands.config.teleportAfterIslandCreation) {
				land.visitAsMember(player);
			}
			player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.land_create.success"));
		}
	}
}
