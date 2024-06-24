package playerworlds.data;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import playerworlds.PlayerworldsMod;

public class PlayerworldsComponents implements WorldComponentInitializer, EntityComponentInitializer {
	public static final ComponentKey<WorldComponent> WORLD_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(PlayerworldsMod.id("world_data"), WorldComponent.class);
	public static final ComponentKey<PlayerComponent> PLAYER_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(PlayerworldsMod.id("player_data"), PlayerComponent.class);

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(WORLD_DATA, WorldComponent::new);
	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(PLAYER_DATA, PlayerComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}
}
