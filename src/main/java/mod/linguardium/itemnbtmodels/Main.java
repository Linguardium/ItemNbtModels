package mod.linguardium.itemnbtmodels;

import mod.linguardium.itemnbtmodels.compat.CITParser;
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
        //ModelLoadingRegistry.INSTANCE.registerAppender(CITParser::ModelLoaderListener);
        //ModelLoadingRegistry.INSTANCE.registerVariantProvider(CITParser.CITVariantProvider::new);
        //TODO: Initializer
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}