package playerlands.mixin.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import playerlands.PlayerlandsMod;
import playerlands.logic.Playerlands;

import java.util.UUID;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow
	@Final
	private static Logger LOGGER;

	@Redirect(method = "save", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false))
	void info(Logger logger, String s, Object o, Object o1) {
		if(o1 instanceof Identifier id) {
			if(id.getNamespace().equals(PlayerlandsMod.MOD_ID) || id.getNamespace().equals("nether")) {
				return;
			}
		}
		logger.info(s, o, o1);
	}

	@Redirect(method = "save", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
	void info(Logger logger, String s, Object o) {
		try {
			if(o instanceof String) {
				// TODO: unused? just to check if parse-able?
				UUID.fromString((String)o);
				return;
			}
		}
		catch (IllegalArgumentException ignored) {}
		logger.info(s, o);
	}

	@Inject(method = "save", at = @At("RETURN"))
	void save(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
		if(flush && !Playerlands.getIslands().isEmpty()) {
			LOGGER.info("ThreadedAnvilChunkStorage: All playerlands lands are saved");
		}
	}
}
