package pigcart.glimchat;

import java.util.UUID;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import pigcart.glimchat.commands.GlimeshBaseCommand;
import pigcart.glimchat.config.ModConfig;

public class GlimChat implements ClientModInitializer {
    public static WebsocketClientEndpoint websocketClientEndpoint;
    @Override
    public void onInitializeClient() {
        ModConfig.getConfig().load();

        GlimeshBaseCommand.registerCommands();
    }

    public static void addGlimeshMessage(String username, String message, Formatting textColor) {
        MutableText usernameText = new LiteralText(username).formatted(Formatting.BLUE).append(": ");
        MutableText messageBodyText = new LiteralText(message).formatted(textColor);
        MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, usernameText.append(messageBodyText), UUID.randomUUID());
    }
    public static void addNotification(MutableText message) {
        MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, message, UUID.randomUUID());
    }

}
