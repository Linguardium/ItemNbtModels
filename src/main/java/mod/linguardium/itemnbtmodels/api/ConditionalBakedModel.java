package mod.linguardium.itemnbtmodels.api;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;

public class ConditionalBakedModel extends BasicBakedModel {
    private final ConditionalOverlayList conditionalOverlayList;

    public ConditionalBakedModel(List<BakedQuad> quads, Map<Direction, List<BakedQuad>> faceQuads, boolean usesAo, boolean isSideLit, boolean hasDepth, Sprite sprite, ModelTransformation modelTransformation, ModelOverrideList modelOverrideList, ConditionalOverlayList conditionalOverlayList) {
        super(quads, faceQuads, usesAo, isSideLit, hasDepth, sprite, modelTransformation, modelOverrideList);
        this.conditionalOverlayList=conditionalOverlayList;
    }
    public ConditionalOverlayList getConditionalOverlayList() {
        return conditionalOverlayList;
    }
}
