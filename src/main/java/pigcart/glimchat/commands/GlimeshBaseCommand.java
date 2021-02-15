package pigcart.glimchat.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.TranslatableText;

public class GlimeshBaseCommand implements ClientCommandPlugin {
    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("glimesh")
                // The command to run if "glimesh" is entered with the argument "enable"
                .then(GlimeshEnableCommand.getArgumentBuilder())
                // If the argument is "disable"
                //.then(GlimeshDisableCommand.getArgumentBuilder())
                // if arg is "watch"
                //.then(GlimeshWatchCommand.getArgumentBuilder())
                .executes(source -> {
                    source.getSource().sendFeedback(new TranslatableText("text.glimchat.command.base.noargs1"));
                    source.getSource().sendFeedback(new TranslatableText("text.glimchat.command.base.noargs2"));
                    return 1;
                })
        );
    }
}
