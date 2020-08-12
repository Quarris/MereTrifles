package quarris.meretrifles.config;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import quarris.meretrifles.MereTrifles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Config(modid = MereTrifles.MODID, name = "World", category = "world")
public class WorldConfig {

    @Config.Name("Block Fluid Placements")
    public static String[] rawBlockFluidPlacements = new String[]{};

    @Config.Ignore
    public static Set<ResourceLocation> blockFluidPlacements = new HashSet<>();

    public static void onConfigsChanged() {
        System.out.println("Updating World Configs");
        blockFluidPlacements.clear();
        blockFluidPlacements.addAll(Arrays.stream(rawBlockFluidPlacements).map(ResourceLocation::new).collect(Collectors.toSet()));
    }
}
