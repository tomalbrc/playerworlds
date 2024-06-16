package playerlands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerlands.PlayerlandsMod;
import playerlands.data.PlayerlandsComponents;
import playerlands.logic.Playerlands;
import playerlands.util.PlayerlandsTexts;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HomeCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("land").then(literal("home").requires(Permissions.require("playerlands.home", true)).executes(context -> {
			var player = context.getSource().getPlayer();
			if(player != null) {
				HomeCommand.run(player);
			}
			return 1;
		})));
		dispatcher.register(literal("land").then(literal("home").requires(Permissions.require("playerlands.home", true)).then(argument("player", word()).suggests((context, builder) -> {
			var player = context.getSource().getPlayer();

			if(player != null) {
				var lands = PlayerlandsComponents.PLAYER_DATA.get(player).getLands();

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
		Playerlands.instance.lands.get(player).ifPresentOrElse(land -> {
			if(player.getWorld().getRegistryKey().getValue().equals(PlayerlandsMod.id(player.getUuid().toString()))) {
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.home.fail"));
			}
			else {
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.home.success"));
				land.visitAsMember(player);
			}
		}, () -> player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.home.no_land")));
	}

	public static void run(ServerPlayerEntity visitor, String landOwner) {
		Playerlands.instance.lands.get(landOwner).ifPresentOrElse(land -> {
			if(visitor.getWorld().getRegistryKey().getValue().equals(PlayerlandsMod.id(land.owner.uuid.toString()))) {
				visitor.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.visit_home.fail", map -> map.put("%owner%", landOwner)));
			}
			else {
				if(land.isMember(visitor)) {
					visitor.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.visit_home.success", map -> map.put("%owner%", landOwner)));
					land.visitAsMember(visitor);
					PlayerlandsComponents.PLAYER_DATA.get(visitor).addIsland(landOwner);
				}
				else {
					visitor.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.visit_home.not_member"));
					PlayerlandsComponents.PLAYER_DATA.get(visitor).removeIsland(landOwner);
				}
			}
		}, () -> {
			visitor.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.visit_home.no_land"));
			PlayerlandsComponents.PLAYER_DATA.get(visitor).removeIsland(landOwner);
		});
	}
}
