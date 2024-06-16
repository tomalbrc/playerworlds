package playerworlds.logic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.world.Difficulty;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import playerworlds.PlayerworldsMod;
import playerworlds.api.PlayerworldsAPI;
import playerworlds.config.PlayerPosition;
import playerworlds.util.PlayerworldsTexts;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.time.Instant;
import java.util.*;

public class Land {
	MinecraftServer server = Playerworlds.instance.server;
	Fantasy fantasy = Playerworlds.instance.fantasy;
	RuntimeWorldConfig landConfig = null;
	public Member owner;
	public ArrayList<Member> members = new ArrayList<>();
	public ArrayList<Member> bans = new ArrayList<>();

	public boolean locked = false;
	public PlayerPosition spawnPos = Playerworlds.config.defaultSpawnPos;
	public PlayerPosition visitsPos = Playerworlds.config.defaultVisitsPos;
	public boolean hasNether = false;
	public long seed = 0L;
	/**
	 * Mark indicates that this land was just created and wasn't visited yet
	 */
	boolean freshCreated = false;
	public Instant created = Instant.now();

	public Land(UUID uuid, String name) {
		// TODO: unused?
		this.owner = new Member(uuid, name);
	}

	public Land(PlayerEntity owner) {
		this.owner = new Member(owner);
	}

	public Land(Member owner) {
		this.owner = owner;
	}

	public static Land fromNbt(NbtCompound nbt) {
		Land land = new Land(Member.fromNbt(nbt.getCompound("owner")));
		land.hasNether = nbt.getBoolean("hasNether");
		land.created = Instant.parse(nbt.getString("created"));
		land.locked = nbt.getBoolean("locked");
		land.seed = nbt.getLong("seed");
		land.freshCreated = nbt.getBoolean("freshCreated");

		land.spawnPos = PlayerPosition.fromNbt(nbt.getCompound("spawnPos"));
		land.visitsPos = PlayerPosition.fromNbt(nbt.getCompound("visitsPos"));

		NbtCompound membersNbt = nbt.getCompound("members");
		int membersSize = membersNbt.getInt("size");
		for(int i = 0; i < membersSize; i++) {
			NbtCompound member = membersNbt.getCompound(String.valueOf(i));
			land.members.add(Member.fromNbt(member));
		}

		NbtCompound bansNbt = nbt.getCompound("bans");
		int bansSize = bansNbt.getInt("size");
		for(int i = 0; i < bansSize; i++) {
			NbtCompound member = bansNbt.getCompound(String.valueOf(i));
			land.bans.add(Member.fromNbt(member));
		}

		return land;
	}

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();
		nbt.put("owner", this.owner.toNbt());
		nbt.putBoolean("hasNether", this.hasNether);
		nbt.putString("created", this.created.toString());
		nbt.putBoolean("locked", this.locked);
		nbt.putLong("seed", this.seed);
		nbt.putBoolean("freshCreated", this.freshCreated);

		nbt.put("spawnPos", this.spawnPos.toNbt());
		nbt.put("visitsPos", this.visitsPos.toNbt());

		NbtCompound membersNbt = new NbtCompound();
		membersNbt.putInt("size", this.members.size());
		for(int i = 0; i < this.members.size(); i++) {
			Member member = this.members.get(i);
			NbtCompound memberNbt = member.toNbt();
			membersNbt.put(Integer.toString(i), memberNbt);
		}
		nbt.put("members", membersNbt);

		NbtCompound bansNbt = new NbtCompound();
		bansNbt.putInt("size", this.bans.size());
		for(int i = 0; i < this.bans.size(); i++) {
			Member bannedMember = this.bans.get(i);
			NbtCompound bannedNbt = bannedMember.toNbt();
			bansNbt.put(Integer.toString(i), bannedNbt);
		}
		nbt.put("bans", bansNbt);

		return nbt;
	}

	public boolean isMember(PlayerEntity player) {
		if(this.owner.uuid.equals(player.getUuid())) {
			return true;
		}
		for(var member : this.members) {
			if(member.uuid.equals(player.getUuid())) return true;
		}
		return false;
	}

	public boolean isMember(String name) {
		if(this.owner.name.equals(name)) {
			return true;
		}
		for(var member : this.members) {
			if(member.name.equals(name)) return true;
		}
		return false;
	}

	public boolean isBanned(PlayerEntity player) {
		for(var bannedMember : this.bans) {
			if(bannedMember.uuid.equals(player.getUuid())) return true;
		}
		return false;
	}

	public boolean isBanned(String player) {
		for(var bannedMember : this.bans) {
			if(bannedMember.name.equals(player)) return true;
		}
		return false;
	}

	public long getSeed() {
		if (this.seed == 0) this.seed = RandomSeed.getSeed();
		return this.seed;
	}

	/**
	 * @return list of players currently on this sland
	 */
	public List<ServerPlayerEntity> getPlayers() {
		// TODO: unused?
		return server.getPlayerManager().getPlayerList().stream().filter(player -> {
			var land = PlayerworldsAPI.getWorld(player);
			return land.isPresent() && land.get().equals(this);
		}).toList();
	}

	public Optional<ServerPlayerEntity> getOwner() {
		return Optional.ofNullable(server.getPlayerManager().getPlayer(this.owner.uuid));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Land isl) return isl.owner.uuid.equals(this.owner.uuid);
		return super.equals(obj);
	}

	public RuntimeWorldHandle getHandle() {
		if (this.landConfig == null) {
			this.landConfig = createIslandConfig();
		}
		RuntimeWorldHandle handle = this.fantasy.getOrOpenPersistentWorld(PlayerworldsMod.id(this.owner.uuid.toString()), this.landConfig);
		WorldBorder border = handle.asWorld().getWorldBorder();
		border.setSize(400);
		return handle;
	}


	private RuntimeWorldConfig createIslandConfig() {
		WorldPreset preset = server.getRegistryManager().get(RegistryKeys.WORLD_PRESET).get(new Identifier("minecraft", "land"));
		ChunkGenerator chunkGenerator;

		if (preset != null) {
			chunkGenerator = preset.getOverworld().get().chunkGenerator();
		} else {
			chunkGenerator = server.getOverworld().getChunkManager().getChunkGenerator();
		}

		return new RuntimeWorldConfig()
				.setDimensionType(DimensionTypes.OVERWORLD)
				.setGenerator(chunkGenerator)
				.setDifficulty(Difficulty.NORMAL)
				.setShouldTickTime(true)
				.setSeed(this.getSeed());
	}

	public void unload() {
		//TODO: never gets called?
		getHandle().unload();

	}

	public ServerWorld getWorld() {
		RuntimeWorldHandle handler = this.getHandle();
		handler.setTickWhenEmpty(false);
		return handler.asWorld();
	}

	public void visit(PlayerEntity visitor, Vec3d pos, float yaw, float pitch) {
		ServerWorld world = this.getWorld();
		visitor.teleport(world, pos.getX(), pos.getY(), pos.getZ(), Set.of(), yaw, pitch);

		if(!isMember(visitor)) {
			this.getOwner().ifPresent(owner -> {
				if(!visitor.getUuid().equals(owner.getUuid())) {
					owner.sendMessage(PlayerworldsTexts.prefixed("message.playerworlds.land_visit.visit", map -> map.put("%visitor%", visitor.getName().getString())));
				}
			});
		}

		PlayerworldsAPI.ON_WORLD_VISIT.invoker().invoke(visitor, world, this);

		if (this.freshCreated) {
			this.onFirstLoad(visitor);
			this.freshCreated = false;
		}
	}

	public void visit(PlayerEntity visitor, PlayerPosition pos) {
		this.visit(visitor, pos.toVec(), pos.yaw, pos.pitch);
	}

	public void visitAsMember(PlayerEntity player) {
		this.visit(player, this.spawnPos);
	}

	public void visitAsVisitor(PlayerEntity player) {
		this.visit(player, this.visitsPos);
	}

	public void onFirstLoad(PlayerEntity player) {
		ServerWorld world = this.getWorld();

		PlayerworldsAPI.ON_WORLD_FIRST_LOAD.invoker().invoke(player, world, this);
	}
}
