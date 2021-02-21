package pigcart.glimchat.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.TranslatableText;
import pigcart.glimchat.GlimChat;
import pigcart.glimchat.WebsocketClientEndpoint;

public class GlimeshDisableCommand {
    public static LiteralArgumentBuilder<CottonClientCommandSource> getArgumentBuilder() {
        return ArgumentBuilders.literal("disable").executes(ctx -> {
            if (GlimChat.websocketClientEndpoint.isClosed()) {
                ctx.getSource().sendFeedback(new TranslatableText("text.glimchat.command.disable.already_disabled"));
                return 1;
            }
            GlimChat.websocketClientEndpoint.close();
            return 1;
        });
    }
}
