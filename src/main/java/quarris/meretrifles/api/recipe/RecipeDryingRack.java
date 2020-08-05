package quarris.meretrifles.api.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.JsonUtils;
import quarris.meretrifles.api.MereTriflesApi;
import quarris.meretrifles.api.helper.ItemHelper;

import java.util.HashMap;
import java.util.Map;

public class RecipeDryingRack extends JsonRecipe {

    public static final Map<ItemStack, RecipeDryingRack> RECIPES = new HashMap<>();

    public static RecipeDryingRack fromInput(ItemStack input) {
        for (Map.Entry<ItemStack, RecipeDryingRack> entry : RECIPES.entrySet()) {
            if (ItemHelper.areStackMetaNbtEqual(input, entry.getKey(), entry.getValue().useTags)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public final ItemStack input;
    public final ItemStack output;
    public final int time;
    public final boolean useTags;

    public RecipeDryingRack(String name, ItemStack output, ItemStack input, int time, boolean useTags) {
        super(name);
        this.output = output;
        this.input = input;
        this.time = time;
        this.useTags = useTags;
    }

    @Override
    public String toString() {
        return "RecipeDryingRack{" +
                "input=" + input +
                ", output=" + output +
                ", time=" + time +
                ", useTags=" + useTags +
                ", name='" + name + '\'' +
                '}';
    }

    public static class RecipeLoader implements IJsonRecipeLoader<RecipeDryingRack> {

        // TODO Check for nbt tags in inputs and outputs
        @Override
        public RecipeDryingRack load(String fileName, JsonObject json) {
            try {
                ItemStack output = ShapedRecipes.deserializeItem(json.getAsJsonObject("output"), false);
                ItemStack input = ShapedRecipes.deserializeItem(json.getAsJsonObject("input"), false);
                int time = JsonUtils.getInt(json, "time");

                RecipeDryingRack recipe = new RecipeDryingRack(fileName, output, input, time, false);
                RECIPES.put(input, recipe);
                return recipe;
            } catch (JsonSyntaxException e) {
                MereTriflesApi.LOGGER.warn("Could not parse drying rack recipe; skipping", e);
            }
            return null;
        }
    }
}
