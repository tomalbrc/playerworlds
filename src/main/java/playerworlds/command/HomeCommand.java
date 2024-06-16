package playerworlds.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerworlds.PlayerworldsMod;
import playerworlds.data.PlayerworldsComponents;
import playerworlds.logic.Playerworlds;
import playerworlds.util.PlayerworldsTexts;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HomeCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("world").then(literal("home").requires(Permissions.require("playerworlds.home", true)).executes(context -> {
			var player = context.getSource().getPlayer();
			if(player != null) {
				HomeCommand.run(player);
			}
			return 1;
		})));
		dispatcher.register(literal("world").then(literal("home").requires(Permissions.require("playerworlds.home", true)).then(argument("player", word()).suggests((context, builder) -> {
			var player = context.getSource().getPlayer();

			if(player != null) {
				var lands = PlayerworldsComponents.PLAYER_DATA.get(player).getLands();

				String remains = builder.getRemaining();

				for(String ownerName : lands) {
					if(ownerName.contains(remains)) {
						builder.suggest(ownerName);
					}
				}
				return builder.buildFuture();
			}
			return builder.buildFuture();
		}).executes(context -> {
			var ownerName = StringArgumentType.getString(context, "player");
			var visitor = context.getSource().getPlayer();
			if(visitor != null) {
				HomeCommand.run(visitor, ownerName);
			}
			return 1;
		}))));
	}

	public static void run(ServerPlayerEntity player) {
		Playerworlds.instance.worlds.get(player).ifPresentOrElse(land -> {
			if(player.getWorld().getRegistryKey().getValue().equals(PlayerworldsMod.id(player.getUuid().toString()))) {
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.home.fail"));
			}
			else {
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.home.success"));
				land.visitAsMember(player);
			}
		}, () -> player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.home.no_land")));
	}

	public static void run(ServerPlayerEntity visitor, String landOwner) {
		Playerworlds.instance.worlds.get(landOwner).ifPresentOrElse(land -> {
			if(visitor.getWorld().getRegistryKey().getValue().equals(PlayerworldsMod.id(land.owner.uuid.toString()))) {
				visitor.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.visit_home.fail", map -> map.put("%owner%", landOwner)));
			}
			else {
				if(land.isMember(visitor)) {
					visitor.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.visit_home.success", map -> map.put("%owner%", landOwner)));
					land.visitAsMember(visitor);
					PlayerworldsComponents.PLAYER_DATA.get(visitor).addIsland(landOwner);
				}
				else {
					visitor.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.visit_home.not_member"));
					PlayerworldsComponents.PLAYER_DATA.get(visitor).removeIsland(landOwner);
				}
			}
		}, () -> {
			visitor.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.visit_home.no_land"));
			PlayerworldsComponents.PLAYER_DATA.get(visitor).removeIsland(landOwner);
		});
	}
}
