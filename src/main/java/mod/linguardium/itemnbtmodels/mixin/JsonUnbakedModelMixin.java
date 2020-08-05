package mod.linguardium.itemnbtmodels.mixin;

import com.mojang.datafixers.util.Either;
import mod.linguardium.itemnbtmodels.api.ConditionalJsonUnbakedModel;
import mod.linguardium.itemnbtmodels.compat.CITUnbakedModel;
import mod.linguardium.itemnbtmodels.compat.CITUnbakedModelBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(JsonUnbakedModel.class)
public class JsonUnbakedModelMixin implements CITUnbakedModelBuilder {
    @Shadow protected Identifier parentId;

    @Shadow @Final private List<ModelElement> elements;

    @Shadow @Final public Map<String, Either<SpriteIdentifier, String>> textureMap;

    @Shadow @Final private boolean ambientOcclusion;

    @Shadow @Final private JsonUnbakedModel.GuiLight guiLight;

    @Shadow @Final private ModelTransformation transformations;

    @Shadow @Final private List<ModelOverride> overrides;

    @Override
    public CITUnbakedModel getCITUnbakedModel(ModelIdentifier mId) {
        return new CITUnbakedModel(mId, parentId,elements,textureMap,ambientOcclusion,guiLight,transformations,overrides);
    }
}
