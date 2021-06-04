package pigcart.glimchat.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.text.TranslatableText;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

public class GlimeshBaseCommand {
    public static void registerCommands() {

        CommandDispatcher<FabricClientCommandSource> commandDispatcher = ClientCommandManager.DISPATCHER;

        commandDispatcher.register(ClientCommandManager.literal("glimesh")
                .then(GlimeshConnectCommand.getArgumentBuilder())
                .then(GlimeshDisableCommand.getArgumentBuilder())
                .then(GlimeshPlayCommand.getArgumentBuilder())
                .executes(source -> {
                    source.getSource().sendFeedback(new TranslatableText("text.glimchat.command.base.noargs1"));
                    source.getSource().sendFeedback(new TranslatableText("text.glimchat.command.base.noargs2"));
                    source.getSource().sendFeedback(new TranslatableText("text.glimchat.command.base.noargs3"));
                    return 1;
                })
        );
    }
}