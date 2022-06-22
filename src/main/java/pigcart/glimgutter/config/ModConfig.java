package pigcart.glimgutter.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "glimgutter")
public class ModConfig implements ConfigData {
    public String sendMessagePrefix = ".";
    public boolean showChatMessages = true;
    public boolean showFollowMessages = true;
    public boolean showSubMessages = true;

    @ConfigEntry.Category(value = "eventCommands")
    public boolean doSubCommand = false;
    @ConfigEntry.Category(value = "eventCommands")
    public String subCommand = "Not implemented yet!";
    @ConfigEntry.Category(value = "eventCommands")
    public boolean doFollowCommand = false;
    @ConfigEntry.Category(value = "eventCommands")
    public String followCommand = "Not implemented yet!";
    @ConfigEntry.Category(value = "eventCommands")
    public boolean doChatCommand = false;
    @ConfigEntry.Category(value = "eventCommands")
    public String chatCommand = "Not implemented yet!";

    @ConfigEntry.Category(value = "glimeshPlays")
    public String glimeshPlaying = "use /glimesh play";

    @ConfigEntry.Category(value = "privateInfo")
    public String authKey = "Not implemented yet!";

    @ConfigEntry.Gui.Excluded
    public String channel = "";
}