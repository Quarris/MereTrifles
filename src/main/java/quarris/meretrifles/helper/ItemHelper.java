package quarris.meretrifles.helper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemHelper {

    public static boolean areStackMetaNbtEqual(ItemStack stack1, ItemStack stack2, boolean compareTags) {
        boolean areTagsEqual = !compareTags || ItemStack.areItemStackTagsEqual(stack1, stack1);
        return areTagsEqual && stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == OreDictionary.WILDCARD_VALUE || stack2.getMetadata() == stack1.getMetadata());
    }

}
