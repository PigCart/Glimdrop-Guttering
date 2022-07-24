package pigcart.glimgutter.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import pigcart.glimgutter.GlimGutter;
import pigcart.glimgutter.GlimeshWebSocketClient;
import pigcart.glimgutter.config.ModConfig;

import java.net.URI;
import java.net.URISyntaxException;

public class GlimeshConnectCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> getArgumentBuilder() {
        return ClientCommandManager.literal("connect")
                .then(ClientCommandManager.argument("channel_name", StringArgumentType.string())
                .executes(ctx -> {
                    ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
                    config.channel = StringArgumentType.getString(ctx, "channel_name");

                    if (GlimGutter.glimeshWebSocketClient != null && GlimGutter.glimeshWebSocketClient.isOpen()) {
                        ctx.getSource().sendFeedback(Component.translatable("text.glimgutter.command.connect.already_enabled"));
                        return 0;
                    }

                    if (config.authKey.equals("")) {
                        if (config.verboseFeedback) GlimGutter.addInfoChatMsg(Component.literal("Connecting to Glimesh without an auth key"));
                        ctx.getSource().sendFeedback(Component.translatable("text.glimgutter.command.connect.connecting"));
                        // open websocket
                        try {
                            GlimGutter.glimeshWebSocketClient = new GlimeshWebSocketClient(new URI("wss://glimesh.tv/api/socket/websocket?vsn=2.0.0&client_id=bfba34fd-f6d6-4e12-873a-f1be401299fd"));
                            GlimGutter.glimeshWebSocketClient.connect();
                            return 1;
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                            return -1;
                        }

                    } else {
                        //TODO: open websocket with auth key, allowing for the user to send messages and mutate other stuff.
                        ctx.getSource().sendFeedback(Component.translatable("text.glimgutter.command.connect.connecting"));
                        try {
                            GlimGutter.glimeshWebSocketClient = new GlimeshWebSocketClient(new URI("wss://glimesh.tv/api/socket/websocket?vsn=2.0.0&client_id=bfba34fd-f6d6-4e12-873a-f1be401299fd"));
                            GlimGutter.glimeshWebSocketClient.connect();
                            return 1;
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                            return -1;
                        }
                    }
    }));
    }
}