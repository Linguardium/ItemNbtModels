package mod.linguardium.itemnbtmodels.api;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static net.minecraft.client.render.model.json.JsonUnbakedModel.Deserializer.resolveReference;

public class ConditionalJsonUnbakedModel extends JsonUnbakedModel {
    public static class ConditionalOverlay {
        Identifier modelId;
        String eval;
        public ConditionalOverlay(Identifier m ,String e){
            this.modelId = m;
            this.eval=e;
        }
        public Identifier getModelId() { return modelId; }
        public String getEval() {
            return eval;
        }
        public boolean matches(ItemStack stack, World world, LivingEntity entity) {
            return NbtMatcher.matches(stack, entity, new NbtMatcher.NbtCheckValue(eval));
        }
    }

    public final List<ConditionalOverlay> conditionalOverlays;
    public ConditionalJsonUnbakedModel(Identifier parentId, List<ModelElement> elements, Map<String, Either<SpriteIdentifier, String>> textureMap, List<ConditionalOverlay> conditionalOverlays, boolean ambientOcclusion, GuiLight guiLight, ModelTransformation transformations, List<ModelOverride> overrides) {
        super(parentId, elements, textureMap, ambientOcclusion, guiLight, transformations, overrides);
        this.conditionalOverlays=conditionalOverlays;
    }
    public static Map<String, ConditionalOverlay> deserializeConditionalOverlays(JsonObject object) {
        Identifier identifier = SpriteAtlasTexture.BLOCK_ATLAS_TEX;
        Map<String, ConditionalOverlay> map = Maps.newHashMap();
        if (object.has("conditional_overlays")) {
            JsonObject jsonObject = JsonHelper.getObject(object, "conditional_overlays");
            for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
                JsonObject obj = element.getValue().getAsJsonObject();
                map.put(element.getKey(),
                        new ConditionalOverlay(
                                new Identifier(JsonHelper.getString(obj,"model")),
                            JsonHelper.getString(obj,"eval"))
                );
            }
        }

        return map;
    }

}
