package mod.linguardium.itemnbtmodels.compat;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import mod.linguardium.itemnbtmodels.Main;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
@Environment(EnvType.CLIENT)
public class CITParser {

    private String LoadPropertiesFile(Identifier id) {
        return "";
    }
    public static void applyPropertiesFiles() {

    }
    public static void ModelLoaderListener(ResourceManager manager, Consumer<ModelIdentifier> processor) {
        List<Identifier> models = Lists.newArrayList(manager.findResources("optifine/cit",
                (string) -> string.endsWith(".json")));
                for(Identifier id : models) {
                    ModelIdentifier modelId = new ModelIdentifier(new Identifier(id.getNamespace(),id.getPath().replace(".json","")),"");
                    processor.accept(modelId);
                }
    }
    public static class CITVariantProvider implements ModelVariantProvider {
        private final ResourceManager manager;
        public CITVariantProvider(ResourceManager resourceManager) {
            this.manager=resourceManager;
        }

        @Override
        public UnbakedModel loadModelVariant(ModelIdentifier mId, ModelProviderContext context) throws ModelProviderException {
            if (mId.getPath().startsWith("optifine/cit")) {
                //JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(identifier2);
                //this.putModel(modelIdentifier, jsonUnbakedModel);
                //this.unbakedModels.put(identifier2, jsonUnbakedModel);
                Resource resource = null;
                Identifier file = new Identifier(mId.getNamespace(),mId.getPath()+".json");
                try {
                    resource = manager.getResource(file);
                    InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                    JsonUnbakedModel ubModel = JsonUnbakedModel.deserialize(reader);
                    ubModel.id=mId.toString();
                    return ubModel;
                } catch (IOException e) {
                    Main.log(Level.ERROR,"Unable to load model json: "+mId);
                }
            }
            return null;
        }
    }
}
