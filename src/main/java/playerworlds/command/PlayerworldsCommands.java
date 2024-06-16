package playerworlds.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import playerworlds.config.PlayerworldsConfigCommands;
import playerworlds.logic.Playerworlds;
import playerworlds.util.PlayerworldsTexts;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PlayerworldsCommands {
	public static final SuggestionProvider<ServerCommandSource> SUGGEST_ISLANDS = (context, builder) -> {
		String remains = builder.getRemaining();

		for(var land : Playerworlds.getIslands().stuck) {
			if(land.owner.name.contains(remains)) {
				builder.suggest(land.owner.name);
			}
		}
		return builder.buildFuture();
	};

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> PlayerworldsCommands.register(dispatcher));
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

		dispatcher.register(literal("force-lands").then(literal("delete").requires(Permissions.require("playerworlds.force.delete", 4)).then(argument("player", word()).suggests(SUGGEST_ISLANDS).executes(context -> {
			var playerName = StringArgumentType.getString(context, "player");

			Playerworlds.getIslands().get(playerName).ifPresentOrElse(land -> {
				Playerworlds.instance.worlds.delete(playerName);
				PlayerworldsTexts.prefixed(context, "message.playerworlds.force.delete.success", map -> map.put("%player%", playerName));
			}, () -> PlayerworldsTexts.prefixed(context, "message.playerworlds.force.delete.fail", map -> map.put("%player%", playerName)));

			return 1;
		}))));

		dispatcher.register(literal("force-lands").then(literal("visit").requires(Permissions.require("playerworlds.force.visit", 4)).then(argument("player", word()).suggests(SUGGEST_ISLANDS).executes(context -> {
			var playerName = StringArgumentType.getString(context, "player");
			var admin = context.getSource().getPlayer();
			if(admin != null) {
				Playerworlds.getIslands().get(playerName).ifPresentOrElse(land -> {
					land.visitAsMember(admin);
					PlayerworldsTexts.prefixed(context, "message.playerworlds.force.visit.success", map -> map.put("%player%", playerName));
				}, () -> PlayerworldsTexts.prefixed(context, "message.playerworlds.force.visit.fail", map -> map.put("%player%", playerName)));
			}
			return 1;
		}))));
		PlayerworldsConfigCommands.init(dispatcher);
	}
}
