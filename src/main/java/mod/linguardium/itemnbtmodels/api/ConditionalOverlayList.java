package mod.linguardium.itemnbtmodels.api;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
@Environment(EnvType.CLIENT)
public class ConditionalOverlayList {
    public static final ConditionalOverlayList EMPTY = new ConditionalOverlayList();
    private final List<ConditionalJsonUnbakedModel.ConditionalOverlay> overlays;
    private final List<BakedModel> models;
    private ConditionalOverlayList() {
        this.models = Collections.emptyList();
        overlays = new ArrayList<>();
    }

    public ConditionalOverlayList(ModelLoader modelLoader, JsonUnbakedModel unbakedModel, Function<Identifier, UnbakedModel> uGetter, List<ConditionalJsonUnbakedModel.ConditionalOverlay> overlays) {
        this.models = overlays.stream().map(overlay -> {
            UnbakedModel unbakedModelx = uGetter.apply(overlay.getModelId());
            return Objects.equals(unbakedModelx, unbakedModel) ? null : modelLoader.bake(overlay.getModelId(), ModelRotation.X0_Y0);
        }).collect(Collectors.toList());
        Collections.reverse(this.models);
        this.overlays = overlays;
    }

    public BakedModel apply(ItemStack stack, ClientWorld world, LivingEntity entity) {
        if (!this.overlays.isEmpty()) {
            for(int i = 0; i < this.overlays.size(); ++i) {
                ConditionalJsonUnbakedModel.ConditionalOverlay conditionalOverlay = this.overlays.get(i);
                if (conditionalOverlay.matches(stack, world, entity)) {
                    BakedModel bakedModel = this.models.get(i);
                    if (bakedModel == null) {
                        return null;
                    }

                    return bakedModel;
                }
            }
        }
        return null;
    }
}


