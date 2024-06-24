package playerworlds.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerworlds.logic.LandStuck;
import playerworlds.logic.Playerworlds;
import playerworlds.util.PlayerworldsTexts;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DeleteCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("world").then(literal("delete").requires(Permissions.require("playerworlds.delete", true)).executes(context -> {
			var player = context.getSource().getPlayer();
			if(player != null) DeleteCommand.warn(player);
			return 1;
		}).then(argument("confirmation", word()).executes(context -> {
			var player = context.getSource().getPlayer();
			String confirmWord = StringArgumentType.getString(context, "confirmation");
			if(player != null) DeleteCommand.run(player, confirmWord);
			return 1;
		}))));
	}

	static void run(ServerPlayerEntity player, String confirmWord) {

		if(confirmWord.equals("CONFIRM")) {
			LandStuck lands = Playerworlds.instance.worlds;

			lands.get(player).ifPresentOrElse(land -> {
				var created = land.created;
				var now = Instant.now();
				var seconds = ChronoUnit.SECONDS.between(created, now);

				if(seconds >= Playerworlds.config.landDeletionCooldown || player.isCreative()) {
					lands.delete(player);
					player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_delete.success"));
				}
				else {
					player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_delete.too_often"));
				}

			}, () -> player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_delete.fail")));
		}
		else {
			player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_delete.warning"));
		}
	}

	static void warn(ServerPlayerEntity player) {
		LandStuck lands = Playerworlds.instance.worlds;

		lands.get(player).ifPresentOrElse(land -> {
			var created = land.created;
			var now = Instant.now();
			var hours = ChronoUnit.HOURS.between(created, now);

			if(hours >= 24 || player.isCreative()) {
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_delete.warning"));
			}
			else {
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_delete.too_often"));
			}

		}, () -> player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.world_delete.fail")));
	}
}
