package pigcart.glimgutter.commands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import pigcart.glimgutter.GlimGutter;
import pigcart.glimgutter.GlimeshPlaysController;

public class GlimeshDisableCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> getArgumentBuilder() {
        return ClientCommandManager.literal("disable").executes(ctx -> {
            if (GlimGutter.glimeshWebSocketClient.isClosed() || GlimGutter.glimeshWebSocketClient ==null) {
                ctx.getSource().sendFeedback(Component.translatable("text.glimgutter.command.disable.already_disabled"));
                return 0;
            } else {
                GlimGutter.glimeshWebSocketClient.close();
                GlimeshPlaysController.glimeshPlays = false;
                return 1;
            }
        });
    }
}
