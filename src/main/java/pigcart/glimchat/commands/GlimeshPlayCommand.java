package pigcart.glimchat.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.TranslatableText;
import pigcart.glimchat.GlimChat;
import pigcart.glimchat.GlimeshPlaysController;
import pigcart.glimchat.config.ModConfig;

public class GlimeshPlayCommand {
    public static LiteralArgumentBuilder<CottonClientCommandSource> getArgumentBuilder() {
        return ArgumentBuilders.literal("play").executes(ctx -> {
            if (GlimChat.websocketClientEndpoint.isClosed() || GlimChat.websocketClientEndpoint==null) {
                ctx.getSource().sendFeedback(new TranslatableText("text.glimchat.command.play.no_connection"));
                return 1;
            }
            ModConfig config = ModConfig.getConfig();
            if (config.isPlayEnabled()) {
                GlimeshPlaysController.glimeshPlays = false;
                config.setPlayEnabled(false);
                config.save();
                ctx.getSource().sendFeedback(new TranslatableText("text.glimchat.command.play.disabled"));
            } else {
                GlimeshPlaysController.glimeshPlays = true;
                config.setPlayEnabled(true);
                config.save();
                ctx.getSource().sendFeedback(new TranslatableText("text.glimchat.command.play.enabled"));
            }
            return 1;
        });
    }
}
