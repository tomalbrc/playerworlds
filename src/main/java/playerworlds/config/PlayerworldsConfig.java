package playerworlds.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import net.fabricmc.loader.api.FabricLoader;
import playerworlds.PlayerworldsMod;
import playerworlds.logic.Playerworlds;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class PlayerworldsConfig {
	public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	@SuppressWarnings("unused")
	public String readDocs = "https://github.com/tyap-lyap/skylands/wiki";
	@JsonAdapter(PlayerPosition.JsonAdapter.class)
	public PlayerPosition defaultSpawnPos = new PlayerPosition(0.5D, 75.0D, 0.5D, 0, 0);
	@JsonAdapter(PlayerPosition.JsonAdapter.class)
	public PlayerPosition defaultVisitsPos = new PlayerPosition(0.5D, 75.0D, 0.5D, 0, 0);
	public boolean hubProtectedByDefault = false;
	public int landDeletionCooldown = 0;//(7 * 24 * 60) * 60;

	public boolean teleportAfterIslandCreation = false;
	public boolean createIslandOnPlayerJoin = false;

	public static void init() {
		Playerworlds.config = PlayerworldsConfig.read();
	}

	public static PlayerworldsConfig read() {
		String filePath = FabricLoader.getInstance().getConfigDir().resolve("playerworlds.json").toString();
		try {
			BufferedReader fixReader = new BufferedReader(new FileReader(filePath));
			var json = GSON.fromJson(fixReader, JsonObject.class);
			boolean fixed = false;

			if(json.getAsJsonObject("defaultSpawnPos").has("field_1352")) {
				var defaultSpawnPos = json.getAsJsonObject("defaultSpawnPos");
				defaultSpawnPos.addProperty("x", defaultSpawnPos.getAsJsonPrimitive("field_1352").getAsDouble());
				defaultSpawnPos.addProperty("y", defaultSpawnPos.getAsJsonPrimitive("field_1351").getAsDouble());
				defaultSpawnPos.addProperty("z", defaultSpawnPos.getAsJsonPrimitive("field_1350").getAsDouble());
				fixed = true;
			}

			if(json.getAsJsonObject("defaultVisitsPos").has("field_1352")) {
				var defaultVisitsPos = json.getAsJsonObject("defaultVisitsPos");
				defaultVisitsPos.addProperty("x", defaultVisitsPos.getAsJsonPrimitive("field_1352").getAsDouble());
				defaultVisitsPos.addProperty("y", defaultVisitsPos.getAsJsonPrimitive("field_1351").getAsDouble());
				defaultVisitsPos.addProperty("z", defaultVisitsPos.getAsJsonPrimitive("field_1350").getAsDouble());
				fixed = true;
			}

			if (fixed) {
				var fixedConfig = GSON.fromJson(json, PlayerworldsConfig.class);
				fixedConfig.save();
				return fixedConfig;
			}

			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			var config = GSON.fromJson(reader, PlayerworldsConfig.class);
			config.save();
			return config;
		}
		catch(FileNotFoundException e) {
			PlayerworldsMod.LOGGER.info("File " + filePath + " is not found! Setting to default.");
			var conf = new PlayerworldsConfig();
			conf.save();
			return conf;
		}
		catch(Exception e) {
			PlayerworldsMod.LOGGER.info("Failed to read playerworlds config due to an exception. " +
					"Please delete playerworlds.json to regenerate config or fix the issue:\n" + e);
			e.printStackTrace();
			System.exit(0);
			return new PlayerworldsConfig();
		}
	}

	public void save() {
		try {
			String filePath = FabricLoader.getInstance().getConfigDir().resolve("playerworlds.json").toString();
			try(FileWriter writer = new FileWriter(filePath)) {
				writer.write(GSON.toJson(this));
			}
		}
		catch(Exception e) {
			PlayerworldsMod.LOGGER.info("Failed to save playerworlds config due to an exception:\n" + e);
		}
	}

}
