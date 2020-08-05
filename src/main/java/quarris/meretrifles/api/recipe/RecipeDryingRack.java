package quarris.meretrifles.api.recipe;

import net.minecraft.item.ItemStack;
import quarris.meretrifles.helper.ItemHelper;

import java.util.HashMap;
import java.util.Map;

public class RecipeDryingRack {

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

    public RecipeDryingRack(ItemStack output, ItemStack input, int time, boolean useTags) {
        this.output = output;
        this.input = input;
        this.time = time;
        this.useTags = useTags;
    }
}
