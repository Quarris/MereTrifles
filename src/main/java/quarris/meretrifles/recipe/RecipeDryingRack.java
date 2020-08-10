package quarris.meretrifles.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import quarris.meretrifles.helper.ItemHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.minecraft.util.JsonUtils.*;
import static quarris.meretrifles.recipe.IJsonRecipeLoader.*;

public class RecipeDryingRack extends JsonRecipe {

    public static final Map<ItemStack, RecipeDryingRack> RECIPES = new HashMap<>();

    public static RecipeDryingRack fromInput(World world, BlockPos pos, ItemStack input) {
        for (Map.Entry<ItemStack, RecipeDryingRack> entry : RECIPES.entrySet()) {
            if (entry.getValue().matches(world, pos, input)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private final ItemStack output;
    private final ItemStack input;
    private final boolean useTags;
    private final int time;
    private final boolean needsSky;
    private final int minLight;
    private final int maxLight;
    private final Set<String> biomes;
    private final boolean blacklistBiomes;
    private final float minTemp;
    private final float maxTemp;
    private final int minY;
    private final int maxY;
    private final Set<Integer> dimensions;
    private final boolean blacklistDimensions;

    public RecipeDryingRack(String name, ItemStack output, ItemStack input, int time, boolean useTags, boolean needsSky, int minLight, int maxLight, Set<String> biomes, boolean blacklistBiomes, float minTemp, float maxTemp, int minY, int maxY, Set<Integer> dimensions, boolean blacklistDimensions) {
        super(name);
        this.output = output;
        this.input = input;
        this.time = time;
        this.useTags = useTags;
        this.needsSky = needsSky;
        this.minLight = minLight;
        this.maxLight = maxLight;
        this.biomes = biomes;
        this.blacklistBiomes = blacklistBiomes;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minY = minY;
        this.maxY = maxY;
        this.dimensions = dimensions;
        this.blacklistDimensions = blacklistDimensions;
    }

    public int getWorkTime() {
        return this.time;
    }

    public ItemStack getOutput() {
        return this.output.copy();
    }

    @Override
    public boolean matches(World world, BlockPos pos, ItemStack... inputs) {
        if (inputs.length != 1) {
            return false;
        }

        // Check Height
        if (pos.getY() < this.minY || pos.getY() > this.maxY)
            return false;


        // Check Biome
        Biome biome = world.getBiome(pos);
        if (this.biomes.contains(biome.getRegistryName().toString()) == this.blacklistBiomes) {
            return false;
        }

        // Check Dimension
        int dimension = world.provider.getDimensionType().getId();
        if (this.dimensions.contains(dimension) == this.blacklistDimensions)
            return false;

        return ItemHelper.areStackMetaNbtEqual(this.input, inputs[0], this.useTags);
    }

    @Override
    public boolean canWork(World world, BlockPos pos) {
        // Check Sky
        if (this.needsSky && !world.canBlockSeeSky(pos)) {
            return false;
        }

        // Check Light
        float light = world.getLightBrightness(pos);
        if (light < this.minLight || light > this.maxLight) {
            return false;
        }

        // Check Temperature
        Biome biome = world.getBiome(pos);
        float temperature = biome.getTemperature(pos);
        return !(temperature < this.minTemp) && !(temperature > this.maxTemp);
    }

    public static class RecipeLoader implements IJsonRecipeLoader<RecipeDryingRack> {

        @Override
        public RecipeDryingRack load(String fileName, JsonObject json) throws Exception {
            ItemStack output = getItemStack(json, "output");
            ItemStack input = getItemStack(json, "input");
            int time = getInt(json, "time");
            boolean needsSky = getBoolean(json, "needsSky", false);
            int minLight = getInt(json, "minLight", 0);
            int maxLight = getInt(json, "maxLight", 15);
            if (minLight > maxLight) {
                int tmp = maxLight;
                maxLight = minLight;
                minLight = tmp;
            }

            Set<String> biomes = getStringSet(json, "biomes");
            boolean blacklistBiomes = getBoolean(json, "blacklistBiomes", biomes.isEmpty());

            float minTemp = getFloat(json, "minTemperature", Float.MIN_VALUE);
            float maxTemp = getFloat(json, "maxTemperature", Float.MAX_VALUE);

            int minY = getInt(json, "minY", 0);
            int maxY = getInt(json, "maxY", 256);

            // TODO Seasons from SereneSeasons

            Set<Integer> dimensions = getIntegerSet(json, "dimensions");
            boolean blacklistDimensions = getBoolean(json, "blacklistDimensions", dimensions.isEmpty());

            RecipeDryingRack recipe = new RecipeDryingRack(fileName, output, input, time, false, needsSky, minLight, maxLight, biomes, blacklistBiomes, minTemp, maxTemp, minY, maxY, dimensions, blacklistDimensions);
            RECIPES.put(input, recipe);
            return recipe;
        }


    }
}
