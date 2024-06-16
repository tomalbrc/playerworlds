package playerworlds.logic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import playerworlds.util.PlayerworldsTexts;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class Invites {
	public ArrayList<Invite> invites = new ArrayList<>();

	public void tick(MinecraftServer server) {
		invites.forEach(Invite::tick);
		invites.removeIf(invite -> invite.ticks == 0 || invite.accepted);
	}

	public void create(Land land, PlayerEntity player) {
		invites.add(new Invite(land, player.getUuid()));
	}

	public Optional<Invite> get(Land land, PlayerEntity player) {
		for(var invite : this.invites) {
			if(invite.land.equals(land) && invite.uuid.equals(player.getUuid())) return Optional.of(invite);
		}
		return Optional.empty();
	}

	public boolean hasInvite(Land land, PlayerEntity player) {
		for(var invite : this.invites) {
			if(invite.land.equals(land) && invite.uuid.equals(player.getUuid())) return true;
		}
		return false;
	}

	public static class Invite {
		public UUID uuid;
		public Land land;

		public boolean accepted = false;
		public int ticks = (60 * 20) * 5;

		public Invite(Land land, UUID uuid) {
			this.uuid = uuid;
			this.land = land;
		}

		public void tick() {
			if(ticks != 0) {
				ticks--;
			}
		}

		public void accept(PlayerEntity player) {
			if(land != null) {
				this.accepted = true;
				land.members.add(new Member(player));
			}
			else {
				player.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.accept.no_land"));
			}
		}
	}
}
