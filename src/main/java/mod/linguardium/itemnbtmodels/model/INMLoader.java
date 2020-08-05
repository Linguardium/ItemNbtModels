package mod.linguardium.itemnbtmodels.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mod.linguardium.itemnbtmodels.Main;
import mod.linguardium.itemnbtmodels.api.ModelOverrideNBTList;
import mod.linguardium.itemnbtmodels.api.NbtMatcher;
import mod.linguardium.itemnbtmodels.mixin.JsonUnbakedModelAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.minecraft.client.render.model.ModelLoader.MISSING;

@Environment(EnvType.CLIENT)
public class INMLoader implements ModelVariantProvider {
    ResourceManager manager = null;
    private static Map<ModelIdentifier, List<ModelOverride>> loadedOverrides= Maps.newHashMap();
    public INMLoader(ResourceManager resourceManager) {
        this.manager=resourceManager;
    }
    @Override
    public UnbakedModel loadModelVariant(ModelIdentifier modelIdentifier, ModelProviderContext modelProviderContext) throws ModelProviderException {
        List<ModelOverride> moList = Lists.newArrayList();
        List<Resource> resourceList = Lists.newArrayList();
        if (!modelIdentifier.getVariant().equals("inventory"))
            return null;
        try {
            resourceList = manager.getAllResources(new Identifier(modelIdentifier.getNamespace(), "models/item/" + modelIdentifier.getPath() + ".json"));
        } catch (IOException e) {
            return null;
        }
        for (int i = 0; i < resourceList.size() - 1; i++) {
            JsonUnbakedModel jm = JsonUnbakedModelAccessor.getJsonGson().fromJson(new InputStreamReader(resourceList.get(i).getInputStream(), StandardCharsets.UTF_8), JsonUnbakedModel.class);
            moList.addAll(jm.getOverrides().stream().filter(o->((ModelOverrideNBTList)o).getNbtPredicates().size()>0).collect(Collectors.toList()));
        }
        if (moList.size() > 0) {
            if (loadedOverrides.containsKey(modelIdentifier)) {
                loadedOverrides.get(modelIdentifier).addAll(moList);
            } else {
                loadedOverrides.put(modelIdentifier, moList);
            }
        }
        return null;
    }
    public static void applyOverrides(ModelLoader loader) {
        loadedOverrides.forEach((mId,list)->{
            UnbakedModel m = loader.getOrLoadModel(mId);
            if (m instanceof JsonUnbakedModel && !((JsonUnbakedModel) m).id.equals(MISSING.toString())) {
                ((JsonUnbakedModel) m).getOverrides().addAll(list);
                //Main.log(Level.WARN,"Applied "+list.size()+" overrides to: "+mId.toString());
            }else{
                Main.log(Level.WARN,"Unable to add overrides to: "+mId.toString());
            }
        });
    }
}
