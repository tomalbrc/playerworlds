package playerlands.command;

import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.TextParserUtils;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Language;

import static net.minecraft.server.command.CommandManager.literal;

public class HelpCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("land").then(literal("help").requires(Permissions.require("playerlands.help", true)).executes(context -> {
			ServerPlayerEntity player = context.getSource().getPlayer();
			if(player != null) {
				HelpCommand.run(player);
			}
			return 1;
		})));
	}

	static void run(ServerPlayerEntity player) {
		Language lang = Language.getInstance();
		String text = "";
		String key = "message.playerlands.help.";

		for(int i = 0; i <= 32; i++) {
			if(lang.hasTranslation(key + i)) {
				text = text + lang.get(key + i);
			}
			if(lang.hasTranslation(key + (i + 1))) {
				text = text + "\n";
			}
			else {
				break;
			}
		}
		player.sendMessage(TextParserUtils.formatText(text));
	}
}
