package quarris.meretrifles.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class JsonRecipe {

    public final String name;

    public JsonRecipe(String name) {
        this.name = name;
    }

    /**
     * Checked once at the start of the craft when the recipe is picked.
     */
    public abstract boolean matches(World world, BlockPos pos, ItemStack... inputs);

    /**
     * Checked every tick to see if the recipe can continue to work.
     * Used if the conditions in the world have changed (such as light level or temperature).
     */
    public abstract boolean canWork(World world, BlockPos pos);

}
