package playerlands.event;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.NetworkUtils;
import net.minecraft.util.thread.ThreadExecutor;
import playerlands.logic.Land;
import playerlands.logic.Playerlands;
import playerlands.logic.Member;
import playerlands.util.PlayerlandsTexts;
import playerlands.util.PlayerlandsLevels;

import java.util.Optional;

@SuppressWarnings("unused")
public class PlayerConnectEvent implements ServerPlayConnectionEvents.Join, ServerPlayConnectionEvents.Disconnect {
	public static final PlayerConnectEvent INSTANCE = new PlayerConnectEvent();

	@Override
	public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		ServerPlayerEntity player = handler.getPlayer();
		Playerlands instance = Playerlands.instance;

		Optional<Land> playerLand = instance.lands.get(player);
		if (playerLand.isPresent()) {
			playerLand.get().owner.name = player.getName().getString();
		}
		else {
			server.execute(() -> {
				Playerlands.getIslands().create(player);
			});
		}

		instance.lands.stuck.forEach(land -> {
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

		PlayerlandsLevels.getLand(player.getWorld()).ifPresent(land -> {
			if(!land.isMember(player) && land.isBanned(player)) {
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.ban_player.ban", map -> map.put("%owner%", land.owner.name)));
				player.sendMessage(PlayerlandsTexts.prefixed("message.playerlands.hub_visit"));
				Playerlands.overworld(player);
			}
		});
	}

	@Override
	public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {

	}
}
