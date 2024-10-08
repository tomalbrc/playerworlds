package playerworlds.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import playerworlds.logic.Playerworlds;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PlayerworldsConfigCommands {

	public static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("force-sl").then(literal("config").requires(Permissions.require("playerworlds.force.config", 4)).then(literal("default-spawn-pos").then(argument("position", blockPos()).executes(context -> {
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			Playerworlds.config.defaultSpawnPos = new PlayerPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			Playerworlds.config.save();
			String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ();
			context.getSource().sendFeedback(() -> Text.of("config.defaultSpawnPos has changed to: " + posText), true);
			return 1;

		}))).then(literal("default-visits-pos").then(argument("position", blockPos()).executes(context -> {
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			Playerworlds.config.defaultVisitsPos = new PlayerPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			Playerworlds.config.save();
			String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ();
			context.getSource().sendFeedback(() -> Text.of("config.defaultVisitsPos has changed to: " + posText), true);
			return 1;

		}))).then(literal("land-deletion-cooldown").then(argument("cooldown", integer()).executes(context -> {
			var cooldown = IntegerArgumentType.getInteger(context, "cooldown");
			Playerworlds.config.landDeletionCooldown = cooldown;
			Playerworlds.config.save();
			context.getSource().sendFeedback(() -> Text.of("config.landDeletionCooldown has changed to: " + cooldown), true);
			return 1;

		}))).then(literal("teleport-after-land-creation").executes(context -> {
			var config = Playerworlds.config;
			config.teleportAfterIslandCreation = !config.teleportAfterIslandCreation;
			config.save();
			context.getSource().sendFeedback(() -> Text.of("config.teleportAfterIslandCreation has changed to: " + config.teleportAfterIslandCreation), true);
			return 1;

		})).then(literal("create-land-on-player-join").executes(context -> {
			var config = Playerworlds.config;
			config.createIslandOnPlayerJoin = !config.createIslandOnPlayerJoin;
			config.save();
			context.getSource().sendFeedback(() -> Text.of("config.createIslandOnPlayerJoin has changed to: " + config.createIslandOnPlayerJoin), true);
			return 1;

		})).then(literal("reload").executes(context -> {
			Playerworlds.config = PlayerworldsConfig.read();
			context.getSource().sendFeedback(() -> Text.of("Config successfully reloaded!"), true);
			return 1;
		}))));
	}
}
