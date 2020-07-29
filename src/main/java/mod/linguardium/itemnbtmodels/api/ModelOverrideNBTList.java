package mod.linguardium.itemnbtmodels.api;

import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public interface ModelOverrideNBTList {
    public void setNbtPredicates(List<NbtMatcher.NbtCheckValue> list);
    public List<NbtMatcher.NbtCheckValue> getNbtPredicates();
}
