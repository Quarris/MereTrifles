package quarris.meretrifles.helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class PlayerHelper {

    public static boolean isPlayerHolding(EntityPlayer player, Item item) {
        return player.getHeldItemMainhand().getItem() == item || player.getHeldItemOffhand().getItem() == item;
    }

}
