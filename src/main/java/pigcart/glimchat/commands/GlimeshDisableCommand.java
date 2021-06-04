package pigcart.glimchat.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;
import pigcart.glimchat.GlimChat;

public class GlimeshDisableCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> getArgumentBuilder() {
        return ClientCommandManager.literal("disable").executes(ctx -> {
            if (GlimChat.websocketClientEndpoint.isClosed() || GlimChat.websocketClientEndpoint==null) {
                ctx.getSource().sendFeedback(new TranslatableText("text.glimchat.command.disable.already_disabled"));
                return 1;
            }
            GlimChat.websocketClientEndpoint.close();
            return 1;
        });
    }
}
