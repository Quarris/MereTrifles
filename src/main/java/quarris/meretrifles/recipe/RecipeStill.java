package quarris.meretrifles.recipe;

import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.minecraft.util.JsonUtils.*;
import static quarris.meretrifles.recipe.IJsonRecipeLoader.*;

public class RecipeStill extends JsonRecipe {

    public static final Map<FluidStack, RecipeStill> RECIPES = new HashMap<>();

    public static RecipeStill fromInput(World world, BlockPos pos, FluidStack input) {
        for (Map.Entry<FluidStack, RecipeStill> entry : RECIPES.entrySet()) {
            if (entry.getValue().matches(world, pos, input)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private final FluidStack output;
    private final FluidStack input;
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

    public RecipeStill(String name, FluidStack output, FluidStack input, int time, boolean useTags, boolean needsSky, int minLight, int maxLight, Set<String> biomes, boolean blacklistBiomes, float minTemp, float maxTemp, int minY, int maxY, Set<Integer> dimensions, boolean blacklistDimensions) {
        super(name);
        this.output = output;
        this.input = input;
        this.useTags = useTags;
        this.time = time;
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

    public FluidStack getRecipeInput() {
        return this.input.copy();
    }

    public FluidStack getRecipeOuput() {
        return this.output.copy();
    }

    public FluidStack createOutput(int amount) {
        FluidStack output = this.output.copy();
        output.amount = amount;
        return output;
    }

    @Override
    public boolean matches(World world, BlockPos pos, Object... inputs) {
        if (inputs.length != 1) {
            return false;
        }

        FluidStack input = (FluidStack) inputs[0];

        return input != null && input.amount >= this.getRecipeInput().amount &&
                input.isFluidEqual(this.input) &&
                matchesHeight(pos, this.minY, this.maxY) &&
                matchesBiome(world, pos, this.biomes, this.blacklistBiomes) &&
                matchesDimension(world, this.dimensions, this.blacklistDimensions);
    }

    @Override
    public boolean canWork(World world, BlockPos pos, Object... inputs) {
        FluidStack input = (FluidStack) inputs[0];

        return input != null && input.isFluidEqual(this.input) &&
                input.amount >= this.getRecipeInput().amount &&
                matchesSky(world, pos, this.needsSky) &&
                matchesLight(world, pos, this.minLight, this.maxLight) &&
                matchesTemperature(world, pos, this.minTemp, this.maxTemp);
    }

    public static class RecipeLoader implements IJsonRecipeLoader<RecipeStill> {

        @Override
        public RecipeStill load(String fileName, JsonObject json) throws Exception {
            FluidStack output = getFluidStack(json, "output");
            FluidStack input = getFluidStack(json, "input");
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

            RecipeStill recipe = new RecipeStill(fileName, output, input, time, false, needsSky, minLight, maxLight, biomes, blacklistBiomes, minTemp, maxTemp, minY, maxY, dimensions, blacklistDimensions);
            RECIPES.put(input, recipe);
            return recipe;
        }
    }
}
