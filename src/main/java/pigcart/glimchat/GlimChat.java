package pigcart.glimchat;

import java.util.UUID;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import pigcart.glimchat.config.ModConfig;

public class GlimChat implements ModInitializer {
    public static WebsocketClientEndpoint websocketClientEndpoint;
    @Override
    public void onInitialize() {
        ModConfig.getConfig().load();
    }

    public static void addGlimeshMessage(String username, String message, Formatting textColor) {
        MutableText usernameText = new LiteralText(username).formatted(Formatting.BLUE).append(": ");
        MutableText messageBodyText = new LiteralText(message).formatted(textColor);
        MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, usernameText.append(messageBodyText), UUID.randomUUID());
    }

}
