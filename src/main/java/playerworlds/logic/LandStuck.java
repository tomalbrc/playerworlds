package playerworlds.logic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import playerworlds.config.PlayerPosition;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class LandStuck {
	public ArrayList<Land> stuck = new ArrayList<>();

	public Land create(PlayerEntity player) {
		for(var land : this.stuck) {
			if(land.owner.uuid.equals(player.getUuid())) return land;
		}
		var land = new Land(player);
		land.freshCreated = true;

		var pos = player.getWorldSpawnPos(land.getHandle().asWorld(), land.getHandle().asWorld().getSpawnPos());

		BlockPos spawnPos = pos;
		land.spawnPos = new PlayerPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
		land.visitsPos = new PlayerPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

		this.stuck.add(land);

		return land;
	}

	public void delete(PlayerEntity player) {
		this.get(player).ifPresent(land -> {
			land.getHandle().delete();
		});
		stuck.removeIf(land -> land.owner.uuid.equals(player.getUuid()));
	}

	public void delete(String playerName) {
		this.get(playerName).ifPresent(land -> {
			land.getHandle().delete();
		});
		stuck.removeIf(land -> land.owner.name.equals(playerName));
	}

	public Optional<Land> get(@Nullable PlayerEntity player) {
		if(player == null) return Optional.empty();
		for(var land : this.stuck) {
			if(land.owner.uuid.equals(player.getUuid())) return Optional.of(land);
		}
		return Optional.empty();
	}

	public Optional<Land> get(String playerName) {
		for(var land : this.stuck) {
			if(land.owner.name.equals(playerName)) return Optional.of(land);
		}
		return Optional.empty();
	}

	public Optional<Land> get(@Nullable UUID playerUuid) {
		if(playerUuid == null) return Optional.empty();
		for(var land : this.stuck) {
			if(land.owner.uuid.equals(playerUuid)) return Optional.of(land);
		}
		return Optional.empty();
	}

	public boolean hasIsland(@Nullable UUID playerUuid) {
		if(playerUuid == null) return false;
		for(var land : this.stuck) {
			if(land.owner.uuid.equals(playerUuid)) return true;
		}
		return false;
	}

	public boolean isEmpty() {
		return stuck.isEmpty();
	}

	public void readFromNbt(NbtCompound nbt) {
		NbtCompound landStuckNbt = nbt.getCompound("landStuck");
		int size = landStuckNbt.getInt("size");

		for(int i = 0; i < size; i++) {
			NbtCompound landNbt = landStuckNbt.getCompound(String.valueOf(i));
			Land land = Land.fromNbt(landNbt);
			if(!this.hasIsland(land.owner.uuid)) {
				this.stuck.add(land);
			}
		}
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtCompound landStuckNbt = new NbtCompound();
		landStuckNbt.putInt("size", this.stuck.size());
		for(int i = 0; i < this.stuck.size(); i++) {
			Land land = this.stuck.get(i);
			NbtCompound landNbt = land.toNbt();
			landStuckNbt.put(Integer.toString(i), landNbt);
		}
		nbt.put("landStuck", landStuckNbt);
	}
}
