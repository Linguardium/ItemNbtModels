package mod.linguardium.itemnbtmodels.mixin;

import com.google.common.collect.Maps;
import com.google.gson.*;
import mod.linguardium.itemnbtmodels.api.ModelOverrideNBTList;
import mod.linguardium.itemnbtmodels.api.NbtMatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ModelOverride.Deserializer.class)
public class ModelOverrideDeserializerMixin {


    protected List<NbtMatcher.NbtCheckValue> deserializeNBTPropertyValues(JsonObject object) {
        Gson gson = new GsonBuilder().create();

        List<NbtMatcher.NbtCheckValue> list = new ArrayList<>();
        if (JsonHelper.hasArray(object,"nbt")) {
            JsonArray jsonArray = JsonHelper.getArray(object, "nbt");
            for (JsonElement jsonElement : jsonArray) {
                NbtMatcher.NbtCheckValue checkval = gson.fromJson(jsonElement, NbtMatcher.NbtCheckValue.class);
                list.add(checkval);
            }
        }
        return list;
    }

    @Inject(at=@At("RETURN"),method="deserialize",cancellable = true)
    private void setNbtMap(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<ModelOverride> cir) {
        ModelOverride mo = cir.getReturnValue();
        ((ModelOverrideNBTList) mo).setNbtPredicates(deserializeNBTPropertyValues(jsonElement.getAsJsonObject()));
        cir.setReturnValue(mo);
    }
    @Inject(at=@At(value="INVOKE",target="Lnet/minecraft/util/JsonHelper;getObject(Lcom/google/gson/JsonObject;Ljava/lang/String;)Lcom/google/gson/JsonObject;"),method="deserializeMinPropertyValues",cancellable = true)
    private void predicatesunnecessary(JsonObject object, CallbackInfoReturnable<Map<Identifier, Float>> cir) {
        if (!object.has("predicates")) {
            cir.setReturnValue(Maps.newLinkedHashMap());
        }
    }
}
