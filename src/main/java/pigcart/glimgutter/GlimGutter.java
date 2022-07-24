package pigcart.glimgutter;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import pigcart.glimgutter.commands.GlimeshBaseCommand;
import pigcart.glimgutter.config.ModConfig;

public class GlimGutter implements ClientModInitializer {
    public static GlimeshWebSocketClient glimeshWebSocketClient;
    public static ModConfig config;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(GlimeshBaseCommand::registerCommands);
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
    public static void addUserChatMsg(String username, String message, ChatFormatting nameFormatting, ChatFormatting msgFormatting) {
        MutableComponent usernameText = Component.literal(username).append(": ").withStyle(nameFormatting);
        MutableComponent messageBodyText = Component.literal(message).withStyle(msgFormatting);
        Minecraft.getInstance().gui.getChat().addMessage(usernameText.append(messageBodyText));
    }
    public static void addInfoChatMsg(MutableComponent message) {
        Minecraft.getInstance().gui.getChat().addMessage(message);
    }
}
//TODO: add mc-publish github action