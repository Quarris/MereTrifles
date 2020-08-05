package quarris.meretrifles.api;

import net.minecraft.item.ItemStack;
import quarris.meretrifles.api.recipe.RecipeDryingRack;

public class MereTriflesApi {

    public static void addDryingRackRecipe(ItemStack output, ItemStack input, int time, boolean useTags) {
        RecipeDryingRack.RECIPES.put(input, new RecipeDryingRack(output, input, time, useTags));
    }

}
