package pigcart.glimgutter.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

public class GlimeshBaseCommand {
    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandBuildContext commandBuildContext) {
        fabricClientCommandSourceCommandDispatcher.register(ClientCommandManager.literal("glimesh")
                .then(GlimeshConnectCommand.getArgumentBuilder())
                .then(GlimeshDisableCommand.getArgumentBuilder())
                .then(GlimeshPlayCommand.getArgumentBuilder())
                .executes(context -> {
                    context.getSource().sendFeedback(Component.translatable("text.glimgutter.command.base.noargs1"));
                    context.getSource().sendFeedback(Component.translatable("text.glimgutter.command.base.noargs2"));
                    context.getSource().sendFeedback(Component.translatable("text.glimgutter.command.base.noargs3"));
                    return 1;
                    // Return a result. -1 is failure, 0 is a pass and 1 is success.
                })
        );
    }
}