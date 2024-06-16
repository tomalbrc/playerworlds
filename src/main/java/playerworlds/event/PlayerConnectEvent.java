package playerworlds.event;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import playerworlds.logic.Land;
import playerworlds.logic.Member;
import playerworlds.logic.Playerworlds;
import playerworlds.util.PlayerworldsLevels;
import playerworlds.util.PlayerworldsTexts;

import java.util.Optional;

@SuppressWarnings("unused")
public class PlayerConnectEvent implements ServerPlayConnectionEvents.Join, ServerPlayConnectionEvents.Disconnect {
	public static final PlayerConnectEvent INSTANCE = new PlayerConnectEvent();

	@Override
	public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		ServerPlayerEntity player = handler.getPlayer();
		playerworlds.logic.Playerworlds instance = Playerworlds.instance;

		Optional<Land> playerLand = instance.worlds.get(player);
		if (playerLand.isPresent()) {
			playerLand.get().owner.name = player.getName().getString();
		}
		else {
			server.execute(() -> {
				Playerworlds.getIslands().create(player);
			});
		}

		instance.worlds.stuck.forEach(land -> {
			for(Member member : land.members) {
				if(member.uuid.equals(player.getUuid())) {
					member.name = player.getName().getString();
				}
			}
			for(Member bannedMember : land.bans) {
				if(bannedMember.uuid.equals(player.getUuid())) {
					bannedMember.name = player.getName().getString();
				}
			}
		});

		PlayerworldsLevels.getLand(player.getWorld()).ifPresent(land -> {
			if(!land.isMember(player) && land.isBanned(player)) {
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.ban_player.ban", map -> map.put("%owner%", land.owner.name)));
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.hub_visit"));
				Playerworlds.overworld(player);
			}
		});
	}

	@Override
	public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {

	}
}
