package playerlands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import playerlands.logic.Playerlands;
import playerlands.util.PlayerlandsTexts;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MemberCommands {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("land").then(literal("members").then(literal("invite").requires(Permissions.require("playerlands.members.invite", true)).then(argument("player", player()).executes(context -> {
			var player = context.getSource().getPlayer();
			var newcomer = EntityArgumentType.getPlayer(context, "player");
			if(player != null && newcomer != null) {
				MemberCommands.invite(player, newcomer);
			}
			return 1;
		})))));
		dispatcher.register(literal("land").then(literal("members").then(literal("remove").requires(Permissions.require("playerlands.members.remove", true)).then(argument("player", word()).suggests((context, builder) -> {
			var player = context.getSource().getPlayer();
			var land = Playerlands.instance.lands.get(player);

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
		Playerlands.instance.lands.get(inviter).ifPresentOrElse(land -> {
			if(land.isMember(newcomer)) {
				inviter.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.invite_member.already_member"));
			}
			else {
				if(Playerlands.instance.invites.hasInvite(land, newcomer)) {
					inviter.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.invite_member.already_invited"));
				}
				else {
					inviter.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.invite_member.success", (map) -> map.put("%newcomer%", newcomer.getName().getString())));

					newcomer.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.invite_member.invite.0", (map) -> map.put("%inviter%", inviter.getName().getString())));
					newcomer.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.invite_member.invite.1", (map) -> map.put("%inviter%", inviter.getName().getString())));

					Playerlands.instance.invites.create(land, newcomer);
				}
			}
		}, () -> inviter.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.invite_member.no_land")));
	}

	static void remove(ServerPlayerEntity player, String removed) {
		Playerlands.instance.lands.get(player).ifPresentOrElse(land -> {
			if(player.getName().getString().equals(removed)) {
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.remove_member.yourself"));
			}
			else {
				if(land.isMember(removed)) {
					land.members.removeIf(member -> member.name.equals(removed));
					player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.remove_member.success", (map) -> map.put("%member%", removed)));
				}
				else {
					player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.remove_member.not_member"));
				}
			}
		}, () -> player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.remove_member.no_land")));
	}
}
