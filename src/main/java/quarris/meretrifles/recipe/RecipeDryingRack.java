package quarris.meretrifles.recipe;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import quarris.meretrifles.helper.ItemHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static net.minecraft.util.JsonUtils.*;
import static quarris.meretrifles.recipe.IJsonRecipeLoader.*;

public class RecipeDryingRack extends JsonRecipe {

    public static final Set<RecipeDryingRack> RECIPES = new HashSet<>();

    public static RecipeDryingRack fromInput(World world, BlockPos pos, ItemStack input) {
        for (RecipeDryingRack recipe : RECIPES) {
            if (recipe.matches(world, pos, input)) {
                return recipe;
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
    private final float minHumidity;
    private final float maxHumidity;
    private final int minY;
    private final int maxY;
    private final Set<Integer> dimensions;
    private final boolean blacklistDimensions;

    public RecipeDryingRack(String name, ItemStack output, ItemStack input, int time, boolean useTags, boolean needsSky, int minLight, int maxLight, Set<String> biomes, boolean blacklistBiomes, float minTemp, float maxTemp, float minHumidity, float maxHumidity, int minY, int maxY, Set<Integer> dimensions, boolean blacklistDimensions) {
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
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
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
    public boolean matches(World world, BlockPos pos, Object... inputs) {
        if (inputs.length != 1) {
            return false;
        }

        return ItemHelper.areStackMetaNbtEqual(this.input, (ItemStack) inputs[0], this.useTags) &&
                matchesHeight(pos, this.minY, this.maxY) &&
                matchesBiome(world, pos, this.biomes, this.blacklistBiomes) &&
                matchesDimension(world, this.dimensions, this.blacklistDimensions);
    }

    @Override
    public boolean canWork(World world, BlockPos pos, Object... inputs) {
        return matchesSky(world, pos, this.needsSky) &&
                matchesLight(world, pos, this.minLight, this.maxLight) &&
                matchesTemperature(world, pos, this.minTemp, this.maxTemp) &&
                matchesHumidity(world, pos, this.minHumidity, this.maxHumidity);
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

            float minHumidity = getFloat(json, "minHumidity", 0);
            float maxHumidity = getFloat(json, "maxHumidity", 1);

            int minY = getInt(json, "minY", 0);
            int maxY = getInt(json, "maxY", 256);

            // TODO Seasons from SereneSeasons

            Set<Integer> dimensions = getIntegerSet(json, "dimensions");
            boolean blacklistDimensions = getBoolean(json, "blacklistDimensions", dimensions.isEmpty());

            RecipeDryingRack recipe = new RecipeDryingRack(fileName, output, input, time, false, needsSky, minLight, maxLight, biomes, blacklistBiomes, minTemp, maxTemp, minHumidity, maxHumidity, minY, maxY, dimensions, blacklistDimensions);
            RECIPES.add(recipe);
            return recipe;
        }


    }
}
