package playerlands.util;

import net.minecraft.nbt.NbtCompound;

import java.time.Instant;
import java.util.UUID;

public class NbtMigrator {

	public static void update(NbtCompound nbt) {
		int format = nbt.getInt("format");

		if(format == 0) {
			from0to1(nbt);
			from1to2(nbt);
			from2to3(nbt);
			from3to4(nbt);
		}
		else if(format == 1) {
			from1to2(nbt);
			from2to3(nbt);
			from3to4(nbt);
		}
		else if(format == 2) {
			from2to3(nbt);
			from3to4(nbt);
		}
		else if(format == 3) {
			from3to4(nbt);
		}
	}

	private static void from0to1(NbtCompound nbt) {
		nbt.putInt("format", 1);
	}

	private static void from1to2(NbtCompound nbt) {
		nbt.putInt("format", 2);
		NbtCompound landStuckNbt = nbt.getCompound("landStuck");
		int size = landStuckNbt.getInt("size");
		for(int i = 0; i < size; i++) {
			NbtCompound landNbt = landStuckNbt.getCompound(String.valueOf(i));
			UUID ownerUuid = landNbt.getUuid("owner");
			NbtCompound member = new NbtCompound();
			member.putString("name", "");
			member.putUuid("uuid", ownerUuid);
			landNbt.put("owner", member);

			NbtCompound membersNbt = new NbtCompound();
			membersNbt.putInt("size", 0);
			landNbt.put("members", membersNbt);
		}
		nbt.put("landStuck", landStuckNbt);
	}

	private static void from2to3(NbtCompound nbt) {
		nbt.putInt("format", 3);
		NbtCompound landStuckNbt = nbt.getCompound("landStuck");
		int size = landStuckNbt.getInt("size");
		for(int i = 0; i < size; i++) {
			NbtCompound landNbt = landStuckNbt.getCompound(String.valueOf(i));
			landNbt.putString("created", Instant.now().toString());
		}
		nbt.put("landStuck", landStuckNbt);
	}

	private static void from3to4(NbtCompound nbt) {
		nbt.putInt("format", 4);
		NbtCompound landStuckNbt = nbt.getCompound("landStuck");
		int size = landStuckNbt.getInt("size");
		for(int i = 0; i < size; i++) {
			NbtCompound landNbt = landStuckNbt.getCompound(String.valueOf(i));
			landNbt.putBoolean("locked", false);

			NbtCompound spawnPosNbt = new NbtCompound();
			spawnPosNbt.putDouble("x", 0.5D);
			spawnPosNbt.putDouble("y", 75D);
			spawnPosNbt.putDouble("z", 0.5D);
			landNbt.put("spawnPos", spawnPosNbt);

			NbtCompound visitsPosNbt = new NbtCompound();
			visitsPosNbt.putDouble("x", 0.5D);
			visitsPosNbt.putDouble("y", 75D);
			visitsPosNbt.putDouble("z", 0.5D);
			landNbt.put("visitsPos", visitsPosNbt);
		}
		nbt.put("landStuck", landStuckNbt);
	}
}
