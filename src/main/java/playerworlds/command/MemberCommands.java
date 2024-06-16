package playerworlds.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerworlds.logic.Playerworlds;
import playerworlds.util.PlayerworldsTexts;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MemberCommands {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("world").then(literal("members").then(literal("invite").requires(Permissions.require("playerworlds.members.invite", true)).then(argument("player", player()).executes(context -> {
			var player = context.getSource().getPlayer();
			var newcomer = EntityArgumentType.getPlayer(context, "player");
			if(player != null && newcomer != null) {
				MemberCommands.invite(player, newcomer);
			}
			return 1;
		})))));
		dispatcher.register(literal("world").then(literal("members").then(literal("remove").requires(Permissions.require("playerworlds.members.remove", true)).then(argument("player", word()).suggests((context, builder) -> {
			var player = context.getSource().getPlayer();
			var land = Playerworlds.instance.worlds.get(player);

			if(player != null && land.isPresent()) {
				var members = land.get().members;
				String remains = builder.getRemaining();

				for(var member : members) {
					if(member.name.contains(remains)) {
						builder.suggest(member.name);
					}
				}
				return builder.buildFuture();
			}
			return builder.buildFuture();
		}).executes(context -> {
			String memberToRemove = StringArgumentType.getString(context, "player");
			var player = context.getSource().getPlayer();
			if(player != null) {
				MemberCommands.remove(player, memberToRemove);
			}
			return 1;
		})))));
	}

	static void invite(ServerPlayerEntity inviter, ServerPlayerEntity newcomer) {
		Playerworlds.instance.worlds.get(inviter).ifPresentOrElse(land -> {
			if(land.isMember(newcomer)) {
				inviter.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.invite_member.already_member"));
			}
			else {
				if (Playerworlds.instance.invites.hasInvite(land, newcomer)) {
					inviter.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.invite_member.already_invited"));
				}
				else {
					inviter.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.invite_member.success", (map) -> map.put("%newcomer%", newcomer.getName().getString())));

					newcomer.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.invite_member.invite.0", (map) -> map.put("%inviter%", inviter.getName().getString())));
					newcomer.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.invite_member.invite.1", (map) -> map.put("%inviter%", inviter.getName().getString())));

					Playerworlds.instance.invites.create(land, newcomer);
				}
			}
		}, () -> inviter.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.invite_member.no_land")));
	}

	static void remove(ServerPlayerEntity player, String removed) {
		Playerworlds.instance.worlds.get(player).ifPresentOrElse(land -> {
			if(player.getName().getString().equals(removed)) {
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.remove_member.yourself"));
			}
			else {
				if(land.isMember(removed)) {
					land.members.removeIf(member -> member.name.equals(removed));
					player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.remove_member.success", (map) -> map.put("%member%", removed)));
				}
				else {
					player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.remove_member.not_member"));
				}
			}
		}, () -> player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.remove_member.no_land")));
	}
}
