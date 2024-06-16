package playerworlds.command;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerworlds.PlayerworldsMod;
import playerworlds.logic.Playerworlds;
import playerworlds.util.PlayerworldsTexts;

import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class VisitCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("world").then(literal("visit").requires(Permissions.require("playerworlds.visit", true)).then(argument("player", player()).executes(context -> {
			var visitor = context.getSource().getPlayer();
			var owner = EntityArgumentType.getPlayer(context, "player");
			if(visitor != null && owner != null) {
				VisitCommand.run(visitor, owner);
			}
			return 1;
		}))));
	}

	public static void run(ServerPlayerEntity visitor, ServerPlayerEntity owner) {
		String ownerName = owner.getName().getString();

		Playerworlds.instance.worlds.get(owner).ifPresentOrElse(land -> {
			if(!land.isMember(visitor) && land.isBanned(visitor)) {
				visitor.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.land_visit.ban", map -> map.put("%owner%", ownerName)));
			}
			else {
				if(!land.locked) {
					if(visitor.getWorld().getRegistryKey().getValue().equals(PlayerworldsMod.id(land.owner.uuid.toString()))) {
						visitor.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.land_visit.fail", map -> map.put("%owner%", ownerName)));
					}
					else {
						visitor.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.land_visit.success", map -> map.put("%owner%", ownerName)));
						land.visitAsVisitor(visitor);
					}
				}
				else {
					visitor.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.land_visit.no_visits", map -> map.put("%owner%", ownerName)));
				}
			}

		}, () -> visitor.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.land_visit.no_land", map -> map.put("%owner%", ownerName))));
	}
}
