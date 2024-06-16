package playerlands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import playerlands.config.PlayerlandsConfigCommands;
import playerlands.logic.Playerlands;
import playerlands.util.PlayerlandsTexts;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class PlayerlandsCommands {
	public static final SuggestionProvider<ServerCommandSource> SUGGEST_ISLANDS = (context, builder) -> {
		String remains = builder.getRemaining();

		for(var land : Playerlands.getIslands().stuck) {
			if(land.owner.name.contains(remains)) {
				builder.suggest(land.owner.name);
			}
		}
		return builder.buildFuture();
	};

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> PlayerlandsCommands.register(dispatcher));
	}

	private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		CreateCommand.init(dispatcher);
		HomeCommand.init(dispatcher);
		VisitCommand.init(dispatcher);
		MemberCommands.init(dispatcher);
		BanCommands.init(dispatcher);
		KickCommand.init(dispatcher);
		HelpCommand.init(dispatcher);
		AcceptCommand.init(dispatcher);
		DeleteCommand.init(dispatcher);
		SettingCommands.init(dispatcher);

		dispatcher.register(literal("force-lands").then(literal("delete").requires(Permissions.require("playerlands.force.delete", 4)).then(argument("player", word()).suggests(SUGGEST_ISLANDS).executes(context -> {
			var playerName = StringArgumentType.getString(context, "player");

			Playerlands.getIslands().get(playerName).ifPresentOrElse(land -> {
				Playerlands.instance.lands.delete(playerName);
				PlayerlandsTexts.prefixed(context, "message.playerlands.force.delete.success", map -> map.put("%player%", playerName));
			}, () -> PlayerlandsTexts.prefixed(context, "message.playerlands.force.delete.fail", map -> map.put("%player%", playerName)));

			return 1;
		}))));

		dispatcher.register(literal("force-lands").then(literal("visit").requires(Permissions.require("playerlands.force.visit", 4)).then(argument("player", word()).suggests(SUGGEST_ISLANDS).executes(context -> {
			var playerName = StringArgumentType.getString(context, "player");
			var admin = context.getSource().getPlayer();
			if(admin != null) {
				Playerlands.getIslands().get(playerName).ifPresentOrElse(land -> {
					land.visitAsMember(admin);
					PlayerlandsTexts.prefixed(context, "message.playerlands.force.visit.success", map -> map.put("%player%", playerName));
				}, () -> PlayerlandsTexts.prefixed(context, "message.playerlands.force.visit.fail", map -> map.put("%player%", playerName)));
			}
			return 1;
		}))));
		PlayerlandsConfigCommands.init(dispatcher);
	}
}
