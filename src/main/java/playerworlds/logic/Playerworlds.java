package playerworlds.logic;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import playerworlds.config.PlayerworldsConfig;
import playerworlds.util.NbtMigrator;
import xyz.nucleoid.fantasy.Fantasy;

// Todo: playerworlds manager
public class Playerworlds {
	public int format = 4;
	public static Playerworlds instance;
	public MinecraftServer server;
	public Fantasy fantasy;
	public playerworlds.logic.LandStuck worlds;
	public playerworlds.logic.Invites invites;

	public static PlayerworldsConfig config;

	public Playerworlds(MinecraftServer server) {
		this.server = server;
		this.fantasy = Fantasy.get(server);
		this.worlds = new LandStuck();
		this.invites = new Invites();
	}

	public void readFromNbt(NbtCompound nbt) {
		NbtCompound skylandsNbt = nbt.getCompound("playerworlds");
		if(skylandsNbt.isEmpty()) return;

		NbtMigrator.update(skylandsNbt);

		this.format = skylandsNbt.getInt("format");
		this.worlds.readFromNbt(skylandsNbt);
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtCompound skylandsNbt = new NbtCompound();

		skylandsNbt.putInt("format", this.format);
		this.worlds.writeToNbt(skylandsNbt);

		nbt.put("playerworlds", skylandsNbt);
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
		return getInstance().worlds;
	}

	public static playerworlds.logic.Playerworlds getInstance() {
		return Playerworlds.instance;
	}

	public void onTick(MinecraftServer server) {
		this.invites.tick(server);
	}
}
