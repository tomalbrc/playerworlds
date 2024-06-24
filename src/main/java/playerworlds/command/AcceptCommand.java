package playerworlds.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerworlds.data.PlayerworldsComponents;
import playerworlds.logic.Playerworlds;
import playerworlds.util.PlayerworldsTexts;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AcceptCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("world").then(literal("accept").requires(Permissions.require("playerworlds.accept", true)).then(argument("player", word()).executes(context -> {
			String inviter = StringArgumentType.getString(context, "player");
			var player = context.getSource().getPlayer();

			if(player != null) {
				AcceptCommand.run(player, inviter);
			}
			return 1;
		}))));
	}

	static void run(ServerPlayerEntity player, String ownerName) {
		if(player.getServer() != null) {
			var inviter = player.getServer().getPlayerManager().getPlayer(ownerName);
			var land = Playerworlds.getIslands().get(inviter);
			if(land.isPresent()) {
				var invite = Playerworlds.instance.invites.get(land.get(), player);
				if(invite.isPresent()) {
					if(!invite.get().accepted) {
						invite.get().accept(player);
						player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.accept.success", map -> map.put("%owner%", ownerName)));
						PlayerworldsComponents.PLAYER_DATA.get(player).addIsland(ownerName);
					}
				}
				else {
					player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.accept.fail"));
				}
			}
			else {
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.accept.no_world"));
			}
		}
		else {
			player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.accept.no_player"));
		}
	}
}
