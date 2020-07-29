package mod.linguardium.itemnbtmodels.mixin;

import mod.linguardium.itemnbtmodels.api.ModelOverrideNBTList;
import mod.linguardium.itemnbtmodels.api.NbtMatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ModelOverride.class)
public class ModelOverrideMixin implements ModelOverrideNBTList {
    List<NbtMatcher.NbtCheckValue> NbtPredicates=new ArrayList<>();

    @Override
    public void setNbtPredicates(List<NbtMatcher.NbtCheckValue> nbtPredicates) {
        this.NbtPredicates=nbtPredicates;
    }

    @Override
    public List<NbtMatcher.NbtCheckValue> getNbtPredicates() {
        return NbtPredicates;
    }
    @Inject(at=@At(value="FIELD", target="net/minecraft/client/render/model/json/ModelOverride.predicateToThresholds:Ljava/util/Map;"),method="matches", cancellable = true)
    private void checkNBTPredicatesFirst(ItemStack stack, ClientWorld world, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        for(NbtMatcher.NbtCheckValue predicate : getNbtPredicates()) {
            if (!NbtMatcher.matches(stack, predicate)) {
                cir.setReturnValue(false);
            }
        }
    }
}
