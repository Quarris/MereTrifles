package quarris.meretrifles.items;

import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.helper.ItemRegistryObject;

public class ItemSextant extends Item {

    public ItemSextant() {
        ItemRegistryObject.create(this)
                .name("sextant")
                .creativeTab(MereTrifles.creativeTab)
                .register();
    }

    public static boolean isSkyClear(World world, BlockPos pos) {
        return world.provider.hasSkyLight() && world.canBlockSeeSky(pos) && !world.isRaining();
    }
}
