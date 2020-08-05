package mod.linguardium.itemnbtmodels.mixin;

import mod.linguardium.itemnbtmodels.Main;
import mod.linguardium.itemnbtmodels.compat.CITParser;
import mod.linguardium.itemnbtmodels.compat.CITUnbakedModel;
import mod.linguardium.itemnbtmodels.compat.CITUnbakedModelBuilder;
import mod.linguardium.itemnbtmodels.model.INMLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Environment(EnvType.CLIENT)
@Mixin(ModelLoader.class)
public class ModelLoaderMixin {
    @Shadow @Final private ResourceManager resourceManager;

    @Inject(at=@At("RETURN"),method="<init>")
    private void applyCITModelPredicates(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int i, CallbackInfo ci) {
        INMLoader.applyOverrides((ModelLoader)(Object)this);
        try {
            CITParser.LoadPropertiesFiles((ModelLoader)(Object)this);
        } catch (IOException e) {
            Main.log(Level.ERROR,"Failed to load properties files.");
            e.printStackTrace();
        }
    }
    @Inject(at=@At("HEAD"),method="loadModelFromJson", cancellable = true)
    private void loadCITModelFromJson(Identifier id, CallbackInfoReturnable<JsonUnbakedModel> cir) {
        if (id.getPath().startsWith("optifine/cit/")) {
            try {
                UnbakedModel cm= new CITParser.CITVariantProvider(resourceManager).loadModelVariant(new ModelIdentifier(id,""),null);
                if (cm instanceof JsonUnbakedModel)
                    cir.setReturnValue((JsonUnbakedModel) cm);
            } catch (ModelProviderException e) {
                e.printStackTrace();
            }
        }
    }
}
