package quarris.meretrifles.items;

import net.minecraft.item.Item;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.helper.ItemRegistryObject;

public class ItemSextant extends Item {

    public ItemSextant() {
        ItemRegistryObject.create(this)
                .name("sextant")
                .creativeTab(MereTrifles.creativeTab)
                .register();
    }
}
