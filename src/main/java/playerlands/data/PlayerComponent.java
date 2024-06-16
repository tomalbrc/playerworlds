package playerlands.data;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import playerlands.logic.Playerlands;
import playerlands.util.PlayerlandsLevels;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerComponent implements ComponentV3 {

	public PlayerEntity player;

	ArrayList<String> lands = new ArrayList<>();

	public PlayerComponent(PlayerEntity player) {
		this.player = player;
	}

	public ArrayList<String> getLands() {
		return lands;
	}

	public void setLands(ArrayList<String> lands) {
		this.lands = lands;
	}

	public void addIsland(String owner) {
		if(!this.lands.contains(owner)) {
			lands.add(owner);
		}
	}

	public void removeIsland(String owner) {
		lands.removeIf(s -> s.equals(owner));
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		NbtCompound landsNbt = tag.getCompound("lands");
		int size = landsNbt.getInt("size");
		for(int i = 0; i < size; i++) {
			String owner = landsNbt.getString(String.valueOf(i));
			this.lands.add(owner);
		}

		if(!tag.getString("lastLand").isEmpty()) {
			Playerlands.instance.lands.get(UUID.fromString(tag.getString("lastLand"))).ifPresent(land -> land.getWorld());
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		NbtCompound landsNbt = new NbtCompound();
		landsNbt.putInt("size", this.lands.size());
		for(int i = 0; i < this.lands.size(); i++) {
			String owner = this.lands.get(i);
			landsNbt.putString(Integer.toString(i), owner);
		}
		tag.put("lands", landsNbt);

		PlayerlandsLevels.getLand(player.getWorld()).ifPresent(land -> tag.putString("lastLand", land.owner.uuid.toString()));
	}
}
