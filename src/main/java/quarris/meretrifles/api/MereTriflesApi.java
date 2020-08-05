package quarris.meretrifles.api;

import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Logger;
import quarris.meretrifles.api.recipe.RecipeDryingRack;

public class MereTriflesApi {

    public static Logger LOGGER;

    public static void addDryingRackRecipe(String name, ItemStack output, ItemStack input, int time, boolean useTags) {
        RecipeDryingRack.RECIPES.put(input, new RecipeDryingRack(name, output, input, time, useTags));
    }

}
