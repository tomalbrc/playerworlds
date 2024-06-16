package playerlands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerlands.data.PlayerlandsComponents;
import playerlands.logic.Playerlands;
import playerlands.util.PlayerlandsTexts;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AcceptCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("land").then(literal("accept").requires(Permissions.require("playerlands.accept", true)).then(argument("player", word()).executes(context -> {
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
			var land = Playerlands.getIslands().get(inviter);
			if(land.isPresent()) {
				var invite = Playerlands.instance.invites.get(land.get(), player);
				if(invite.isPresent()) {
					if(!invite.get().accepted) {
						invite.get().accept(player);
						player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.accept.success", map -> map.put("%owner%", ownerName)));
						PlayerlandsComponents.PLAYER_DATA.get(player).addIsland(ownerName);
					}
				}
				else {
					player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.accept.fail"));
				}
			}
			else {
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.accept.no_land"));
			}
		}
		else {
			player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.accept.no_player"));
		}
	}
}
