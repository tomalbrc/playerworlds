package playerlands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import playerlands.logic.Playerlands;
import playerlands.util.PlayerlandsTexts;
import playerlands.config.PlayerPosition;

import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SettingCommands {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("land").then(literal("settings").then(literal("toggle-visits").requires(Permissions.require("playerlands.settings.lock", true)).executes(context -> {
			var player = context.getSource().getPlayer();
			if(player != null) {
				SettingCommands.toggleVisits(player);
			}
			return 1;
		})).then(literal("set-spawn-pos").requires(Permissions.require("playerlands.settings.spawn.position", true)).then(argument("position", blockPos()).executes(context -> {
			var player = context.getSource().getPlayer();
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			if(player != null) {
				SettingCommands.setSpawnPos(player, pos, 0, 0);
			}
			return 1;
		}).then(argument("yaw", IntegerArgumentType.integer()).then(argument("pitch", IntegerArgumentType.integer()).executes(context -> {
			var player = context.getSource().getPlayer();
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			int yaw = IntegerArgumentType.getInteger(context, "yaw");
			int pitch = IntegerArgumentType.getInteger(context, "pitch");

			SettingCommands.setSpawnPos(player, pos, yaw, pitch);
			return 1;
		}))))).then(literal("set-visits-pos").requires(Permissions.require("playerlands.settings.visits.position", true)).then(argument("position", blockPos()).executes(context -> {
			var player = context.getSource().getPlayer();
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			if(player != null) {
				SettingCommands.setVisitsPos(player, pos, 0, 0);
			}
			return 1;
		}).then(argument("yaw", IntegerArgumentType.integer()).then(argument("pitch", IntegerArgumentType.integer()).executes(context -> {
			var player = context.getSource().getPlayer();
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			int yaw = IntegerArgumentType.getInteger(context, "yaw");
			int pitch = IntegerArgumentType.getInteger(context, "pitch");

			SettingCommands.setVisitsPos(player, pos, yaw, pitch);
			return 1;
		})))))));
	}

	static void toggleVisits(ServerPlayerEntity player) {
		Playerlands.instance.lands.get(player).ifPresentOrElse(land -> {
			if(land.locked) {
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.settings.unlock"));
				land.locked = false;
			}
			else {
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.settings.lock"));
				land.locked = true;
			}

		}, () -> player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.settings.no_land")));
	}

	static void setSpawnPos(ServerPlayerEntity player, BlockPos pos, int yaw, int pitch) {
		Playerlands.instance.lands.get(player).ifPresentOrElse(land -> {
			land.spawnPos = new PlayerPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, yaw, pitch);
			String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + yaw + " " + pitch;
			player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.settings.spawn_pos_change", map -> map.put("%pos%", posText)));

		}, () -> player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.settings.no_land")));
	}

	static void setVisitsPos(ServerPlayerEntity player, BlockPos pos, int yaw, int pitch) {
		Playerlands.instance.lands.get(player).ifPresentOrElse(land -> {
			land.visitsPos = new PlayerPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, yaw, pitch);
			String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + yaw + " " + pitch;
			player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.settings.visits_pos_change", map -> map.put("%pos%", posText)));

		}, () -> player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.settings.no_land")));
	}
}
