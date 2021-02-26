package pigcart.glimchat.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<Screen>) screen -> {
            ConfigBuilder builder = ConfigBuilder.create();
            builder.setTitle(new TranslatableText("config.glimchat.title"));
            builder.setSavingRunnable(() -> ModConfig.getConfig().save());


            ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

            ConfigCategory defaultCategory = builder.getOrCreateCategory(new TranslatableText("config.glimchat.category.default"));
            defaultCategory.addEntry(entryBuilder
                    .startStrField(new TranslatableText("config.glimchat.default.oauthKey"), ModConfig.getConfig().getOauthKey())
                    .setSaveConsumer((s -> ModConfig.getConfig().setOauthKey(s)))
                    .setTooltip(new TranslatableText("config.glimchat.default.oauthKey.tooltip"))
                    .setDefaultValue(ModConfig.DEFAULT_OAUTH_KEY)
                    .build());

            ConfigCategory cosmeticsCategory = builder.getOrCreateCategory(new TranslatableText("config.glimchat.category.cosmetics"));
            cosmeticsCategory.addEntry(entryBuilder
                    .startStrField(new TranslatableText("config.glimchat.cosmetics.prefix"), ModConfig.getConfig().getPrefix())
                    .setSaveConsumer((s -> ModConfig.getConfig().setPrefix(s)))
                    .setTooltip(new TranslatableText("config.glimchat.cosmetics.prefix.tooltip"))
                    .setDefaultValue(ModConfig.DEFAULT_PREFIX)
                    .build());

            return builder.build();
        };
    }
}
