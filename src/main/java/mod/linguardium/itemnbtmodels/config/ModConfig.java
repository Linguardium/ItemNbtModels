package mod.linguardium.itemnbtmodels.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
@Config.Gui.Background("textures/block/cyan_shulker_box.png")
@Config(name="itemnbtmodels")
public class ModConfig implements ConfigData {
    boolean LoadOverridenPredicates = false;
    boolean AttemptLoadingProperties = false;
}
