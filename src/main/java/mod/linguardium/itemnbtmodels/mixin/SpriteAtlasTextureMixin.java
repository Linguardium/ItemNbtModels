package mod.linguardium.itemnbtmodels.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin {
    @Inject(at=@At("HEAD"),method="getTexturePath", cancellable = true)
    private void CITTexturePath(Identifier id, CallbackInfoReturnable<Identifier> cir) {
        if (id.getPath().contains("/cit/")) {
            cir.setReturnValue(new Identifier(id.getNamespace(), id.getPath() + ".png"));
        }
    }
}
