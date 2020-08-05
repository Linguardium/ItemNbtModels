package mod.linguardium.itemnbtmodels.compat;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import mod.linguardium.itemnbtmodels.mixin.JsonUnbakedModelAccessor;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.File;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

public class CITUnbakedModel extends JsonUnbakedModel {
    public CITUnbakedModel(ModelIdentifier mId, Identifier parentId, List<ModelElement> elements, Map<String, Either<SpriteIdentifier, String>> textureMap, boolean ambientOcclusion, GuiLight guiLight, ModelTransformation transformations, List<ModelOverride> overrides) {
        super(parentId, elements, textureMap, ambientOcclusion, guiLight, transformations, overrides);
        this.textureMap.replaceAll((k, v)->{
            if (v.left().isPresent()) {
                SpriteIdentifier sId = v.left().get();
                Path imagePath = Paths.get(mId.getPath(),"..",sId.getTextureId().getPath()).normalize();
                return Either.left(new SpriteIdentifier(sId.getAtlasId(), new Identifier(mId.getNamespace(), imagePath.toString().replace(File.separatorChar,'/') )));
            }
            return Either.right(v.right().get());
        });
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return super.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences);
    }

    @Override
    public SpriteIdentifier resolveSprite(String spriteName) {
        return super.resolveSprite(spriteName);
    }

}
