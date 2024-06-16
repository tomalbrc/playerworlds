package playerlands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerlands.logic.Member;
import playerlands.logic.Playerlands;
import playerlands.util.PlayerlandsTexts;
import playerlands.util.PlayerlandsLevels;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BanCommands {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("land").then(literal("ban").requires(Permissions.require("playerlands.ban", true)).then(argument("player", player()).executes(context -> {
			var player = context.getSource().getPlayer();
			var bannedPlayer = EntityArgumentType.getPlayer(context, "player");
			if(player != null && bannedPlayer != null) {
				BanCommands.ban(player, bannedPlayer);
			}
			return 1;
		}))));

		dispatcher.register(literal("land").then(literal("unban").then(argument("player", word()).suggests((context, builder) -> {
			var player = context.getSource().getPlayer();

			if(player != null) {
				var land = Playerlands.instance.lands.get(player);
				if(land.isPresent()) {
					var bans = land.get().bans;

					String remains = builder.getRemaining();

					for(var member : bans) {
						if(member.name.contains(remains)) {
							builder.suggest(member.name);
						}
					}
					return builder.buildFuture();
				}
			}
			return builder.buildFuture();
		}).executes(context -> {
			String unbanned = StringArgumentType.getString(context, "player");
			var player = context.getSource().getPlayer();

			if(player != null) {
				BanCommands.unban(player, unbanned);
			}
			return 1;
		}))));
	}

	static void ban(ServerPlayerEntity player, ServerPlayerEntity banned) {
		Playerlands.instance.lands.get(player).ifPresentOrElse(land -> {
			if(player.getName().getString().equals(banned.getName().getString())) {
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.ban_player.yourself"));
			}
			else {
				if(land.isMember(banned)) {
					player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.ban_player.member"));
				}
				else {
					if(land.isBanned(banned)) {
						player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.ban_player.fail"));
					}
					else {
						land.bans.add(new Member(banned));
						player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.ban_player.success", map -> map.put("%player%", banned.getName().getString())));
						banned.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.ban_player.ban", map -> map.put("%owner%", land.owner.name)));

						PlayerlandsLevels.getLand(banned.getWorld()).ifPresent(isl -> {
							if(isl.owner.uuid.equals(land.owner.uuid)) {
								banned.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.hub_visit"));
								Playerlands.overworld(banned);
							}
						});
					}
				}
			}
		}, () -> player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.ban_player.no_land")));
	}

	static void unban(ServerPlayerEntity player, String unbanned) {
		Playerlands.instance.lands.get(player).ifPresentOrElse(land -> {
			if(!land.isBanned(unbanned)) {
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.unban_player.fail"));
			}
			else {
				land.bans.removeIf(member -> member.name.equals(unbanned));
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.unban_player.success", map -> map.put("%player%", unbanned)));
			}
		}, () -> player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.unban_player.no_land")));
	}
}
