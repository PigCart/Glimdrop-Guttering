package pigcart.glimgutter.commands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import pigcart.glimgutter.GlimGutter;
import pigcart.glimgutter.GlimeshPlaysController;

public class GlimeshPlayCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> getArgumentBuilder() {
        return ClientCommandManager.literal("play").executes(context -> {
            if (GlimGutter.glimeshWebSocketClient.isClosed() || GlimGutter.glimeshWebSocketClient == null) {
                context.getSource().sendFeedback(Component.translatable("text.glimgutter.command.play.no_connection"));
                return 0;
            }
            if (GlimeshPlaysController.glimeshPlays) {
                GlimeshPlaysController.glimeshPlays = false;
                context.getSource().sendFeedback(Component.translatable("text.glimgutter.command.play.disabled"));
            } else {
                GlimeshPlaysController.glimeshPlays = true;
                context.getSource().sendFeedback(Component.translatable("text.glimgutter.command.play.enabled"));
            }
            return 1;
        });
    }
}
