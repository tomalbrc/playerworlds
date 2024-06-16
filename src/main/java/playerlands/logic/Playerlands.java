package playerlands.logic;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import playerlands.config.PlayerlandsConfig;
import playerlands.util.NbtMigrator;
import xyz.nucleoid.fantasy.Fantasy;

// Todo: playerlands manager
public class Playerlands {
	public int format = 4;
	public static Playerlands instance;
	public MinecraftServer server;
	public Fantasy fantasy;
	public LandStuck lands;
	public Invites invites;

	public static PlayerlandsConfig config;

	public Playerlands(MinecraftServer server) {
		this.server = server;
		this.fantasy = Fantasy.get(server);
		this.lands = new LandStuck();
		this.invites = new Invites();
	}

	public void readFromNbt(NbtCompound nbt) {
		NbtCompound skylandsNbt = nbt.getCompound("playerlands");
		if(skylandsNbt.isEmpty()) return;

		NbtMigrator.update(skylandsNbt);

		this.format = skylandsNbt.getInt("format");
		this.lands.readFromNbt(skylandsNbt);
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtCompound skylandsNbt = new NbtCompound();

		skylandsNbt.putInt("format", this.format);
		this.lands.writeToNbt(skylandsNbt);

		nbt.put("playerlands", skylandsNbt);
	}

	public static void overworld(ServerPlayerEntity player) {
		if (player.getServer() != null) {
			ServerWorld level = player.getServer().getOverworld();
			player.teleport(level, level.getSpawnPos().getX(), level.getSpawnPos().getY(), level.getSpawnPos().getZ(),level.getSpawnAngle(),0);
		}
	}

	public static MinecraftServer getServer() {
		return getInstance().server;
	}

	public static LandStuck getIslands() {
		return getInstance().lands;
	}

	public static Playerlands getInstance() {
		return Playerlands.instance;
	}

	public void onTick(MinecraftServer server) {
		this.invites.tick(server);
	}
}
