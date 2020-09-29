package quarris.meretrifles.recipe;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Set;

public abstract class JsonRecipe {

    public final String name;

    public JsonRecipe(String name) {
        this.name = name;
    }

    /**
     * Checked once at the start of the craft when the recipe is picked.
     */
    public abstract boolean matches(World world, BlockPos pos, Object... inputs);

    /**
     * Checked every tick to see if the recipe can continue to work.
     * Used if the conditions in the world have changed (such as light level or temperature).
     */
    public abstract boolean canWork(World world, BlockPos pos, Object... inputs);

    protected boolean matchesHeight(BlockPos pos, int minY, int maxY) {
        return pos.getY() >= minY && pos.getY() <= maxY;
    }

    protected boolean matchesBiome(World world, BlockPos pos, Set<String> biomes, boolean isBlacklist) {
        return biomes.contains(world.getBiome(pos).getRegistryName().toString()) != isBlacklist;
    }

    protected boolean matchesDimension(World world, Set<Integer> dimensions, boolean isBlacklist) {
        int dimension = world.provider.getDimensionType().getId();
        return dimensions.contains(dimension) != isBlacklist;
    }

    protected boolean matchesSky(World world, BlockPos pos, boolean needsSky) {
        return !needsSky || world.canBlockSeeSky(pos);
    }

    protected boolean matchesLight(World world, BlockPos pos, int minLight, int maxLight) {
        float light = world.getLightBrightness(pos);
        return light >= minLight && light <= maxLight;
    }

    protected boolean matchesTemperature(World world, BlockPos pos, float minTemp, float maxTemp) {
        Biome biome = world.getBiome(pos);
        float temperature = biome.getTemperature(pos);
        return temperature >= minTemp && temperature <= maxTemp;
    }

    protected boolean matchesHumidity(World world, BlockPos pos, float minHumidity, float maxHumidity) {
        Biome biome = world.getBiome(pos);
        float humidity = biome.getRainfall();
        return humidity >= minHumidity && humidity <= maxHumidity;
    }
}
