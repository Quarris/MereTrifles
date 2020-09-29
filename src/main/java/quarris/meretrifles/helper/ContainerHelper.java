package quarris.meretrifles.helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import java.util.function.Function;

public class ContainerHelper {

    public static void addPlayerSlots(InventoryPlayer playerInventory, int startX, int startY, Function<Slot, Slot> slotAdder) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                slotAdder.apply(new Slot(playerInventory, j + i * 9 + 9, startX + j * 18, startY + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            slotAdder.apply(new Slot(playerInventory, k, startX + k * 18, startY + 58));
        }
    }

}
