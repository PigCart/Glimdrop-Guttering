package pigcart.glimchat.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import pigcart.glimchat.GlimChat;
import pigcart.glimchat.WebsocketClientEndpoint;
import pigcart.glimchat.config.ModConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GlimeshEnableCommand {
    public static boolean alreadyEnabled = false;
    public static LiteralArgumentBuilder<CottonClientCommandSource> getArgumentBuilder() {
        return ArgumentBuilders.literal("enable")
                .then(ArgumentBuilders.argument("channel_name", StringArgumentType.string())
                .executes(ctx -> {
                    String channelName = StringArgumentType.getString(ctx, "channel_name");

                    ModConfig config = ModConfig.getConfig();
                    config.setChannel(channelName);
                    config.save();
                    if (alreadyEnabled) {
                        ctx.getSource().sendFeedback(new TranslatableText("text.glimchat.command.enable.already_enabled"));
                        return 1;
                        // Return a result. -1 is failure, 0 is a pass and 1 is success.
                    }

                    if (config.getOauthKey().equals("")) {
                        ctx.getSource().sendFeedback(new TranslatableText("text.glimchat.command.enable.set_config"));
                        return -1;
                    }

                    if (config.getChannel().equals("")) {
                        ctx.getSource().sendFeedback(new TranslatableText("text.glimchat.command.enable.set_channel"));
                    }

                    try {
                        // open websocket
                        GlimChat.addGlimeshMessage("", "Opening connection to Glimesh.", Formatting.BOLD);
                        final WebsocketClientEndpoint c = new WebsocketClientEndpoint(new URI("wss://glimesh.tv/api/socket/websocket?vsn=2.0.0&client_id=535cd97ab33c5cd159859d4a4849eb7a635f96fead877dd51004c4abb48e1d3d"));
                        c.connect();
                        return 1;

                    } catch (URISyntaxException ex) {
                        System.err.println("URISyntaxException exception: " + ex.getMessage());
                        GlimChat.addGlimeshMessage("URISyntaxException exception", ex.getMessage(), Formatting.BOLD);
                        return -1;
                    }
    }));
    }
}
