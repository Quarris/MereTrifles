package quarris.meretrifles.config;

import net.minecraftforge.common.config.Config;
import quarris.meretrifles.MereTrifles;

@Config(modid = MereTrifles.MODID, category = "")
public class ModConfig {

    @Config.Name("General")
    public static GeneralConfig general = new GeneralConfig();


    @Config.Name("World")
    public static WorldConfig world = new WorldConfig();

    public static void onConfigChanged() {
        general.onConfigsChanged();
        world.onConfigsChanged();
    }

}
