package playerworlds.command;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerworlds.logic.Playerworlds;
import playerworlds.util.PlayerworldsLevels;
import playerworlds.util.PlayerworldsTexts;

import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class KickCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("world").then(literal("kick").requires(Permissions.require("playerworlds.kick", true)).then(argument("player", player()).executes(context -> {
			var player = context.getSource().getPlayer();
			var kickedPlayer = EntityArgumentType.getPlayer(context, "player");
			if(player != null && kickedPlayer != null) {
				KickCommand.run(player, kickedPlayer);
			}
			return 1;
		}))));
	}

	static void run(ServerPlayerEntity player, ServerPlayerEntity kicked) {
		Playerworlds.instance.worlds.get(player).ifPresentOrElse(land -> {
			if(player.getName().getString().equals(kicked.getName().getString())) {
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.kick_visitor.yourself"));
			}
			else {
				if(land.isMember(kicked)) {
					player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.kick_visitor.member"));
				}
				else {
					PlayerworldsLevels.getLand(kicked.getWorld()).ifPresent(isl -> {
						if(isl.owner.uuid.equals(land.owner.uuid)) {
							player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.kick_visitor.success", map -> map.put("%player%", kicked.getName().getString())));

							kicked.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.kick_visitor.kick", map -> map.put("%owner%", player.getName().getString())));
							kicked.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.hub_visit"));
							Playerworlds.overworld(kicked);
						}
						else {
							player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.kick_visitor.fail", map -> map.put("%player%", kicked.getName().getString())));
						}
					});
				}
			}
		}, () -> player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.kick_visitor.no_land")));
	}
}
