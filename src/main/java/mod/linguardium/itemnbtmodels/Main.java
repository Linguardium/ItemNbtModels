package mod.linguardium.itemnbtmodels;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import mod.linguardium.itemnbtmodels.compat.CITParser;
import mod.linguardium.itemnbtmodels.config.ModConfig;
import mod.linguardium.itemnbtmodels.model.INMLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.ModelAppender;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.function.Consumer;
@Environment(EnvType.CLIENT)
public class Main implements ClientModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "itemnbtmodels";
    public static final String MOD_NAME = "ItemNbtModels";

    @Override
    public void onInitializeClient() {
        log(Level.INFO, "Initializing");
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);

        ModelLoadingRegistry.INSTANCE.registerAppender(CITParser::ModelLoaderListener);
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(CITParser.CITVariantProvider::new);
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(INMLoader::new);

        //TODO: Initializer
    }
    public static ModConfig config() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}