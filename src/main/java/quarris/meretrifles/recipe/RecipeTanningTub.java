package quarris.meretrifles.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import quarris.meretrifles.helper.ItemHelper;

import java.util.HashSet;
import java.util.Set;

import static net.minecraft.util.JsonUtils.*;
import static quarris.meretrifles.recipe.IJsonRecipeLoader.*;

public class RecipeTanningTub extends JsonRecipe {

    public static final Set<RecipeTanningTub> RECIPES = new HashSet<>();

    public static RecipeTanningTub fromInput(World world, BlockPos pos, ItemStack item, FluidStack fluid) {
        for (RecipeTanningTub recipe : RECIPES) {
            if (recipe.matches(world, pos, item, fluid)) {
                return recipe;
            }
        }

        return null;
    }

    public final ItemStack output;
    public final ItemStack itemInput;
    public final FluidStack fluidInput;
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

    public RecipeTanningTub(String name, ItemStack output, ItemStack itemInput, FluidStack fluidInput, int time, boolean needsSky, int minLight, int maxLight, Set<String> biomes, boolean blacklistBiomes, float minTemp, float maxTemp, int minY, int maxY, Set<Integer> dimensions, boolean blacklistDimensions) {
        super(name);
        this.fluidInput = fluidInput;
        this.itemInput = itemInput;
        this.output = output;
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

    public FluidStack getRecipeInput() {
        return this.fluidInput.copy();
    }

    @Override
    public boolean matches(World world, BlockPos pos, Object... inputs) {
        if (inputs.length != 2) {
            return false;
        }

        FluidStack fluid = (FluidStack) inputs[0];
        ItemStack item = (ItemStack) inputs[1];

        return fluid != null && fluid.amount >= this.getRecipeInput().amount &&
                fluid.isFluidEqual(this.fluidInput) &&
                ItemHelper.areStackMetaNbtEqual(this.itemInput, item, false) &&
                matchesHeight(pos, this.minY, this.maxY) &&
                matchesBiome(world, pos, this.biomes, this.blacklistBiomes) &&
                matchesDimension(world, this.dimensions, this.blacklistDimensions);
    }

    @Override
    public boolean canWork(World world, BlockPos pos, Object... inputs) {
        return matchesSky(world, pos, this.needsSky) &&
                matchesLight(world, pos, this.minLight, this.maxLight) &&
                matchesTemperature(world, pos, this.minTemp, this.maxTemp);
    }

    public static class RecipeLoader implements IJsonRecipeLoader<RecipeTanningTub> {


        @Override
        public RecipeTanningTub load(String fileName, JsonObject json) throws Exception {
            ItemStack output = getItemStack(json, "output");
            ItemStack itemInput = getItemStack(json, "itemInput");
            FluidStack fluidInput = getFluidStack(json, "fluidInput");
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

            RecipeTanningTub recipe = new RecipeTanningTub(fileName, output, itemInput, fluidInput, time, needsSky, minLight, maxLight, biomes, blacklistBiomes, minTemp, maxTemp, minY, maxY, dimensions, blacklistDimensions);
            RECIPES.add(recipe);
            return recipe;
        }
    }
}
