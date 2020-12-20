package quarris.meretrifles.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.blocks.BlockDryingRack;
import quarris.meretrifles.blocks.ModBlocks;
import quarris.meretrifles.blocks.tiles.TileDryingRack;
import quarris.meretrifles.blocks.tiles.TileStill;
import quarris.meretrifles.blocks.tiles.TileTanningTub;
import quarris.meretrifles.helper.BlockRegistryObject;
import quarris.meretrifles.items.ItemSextant;
import quarris.meretrifles.items.ModItems;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MereTrifles.MODID)
public class RegistryHandler {

    public static final List<Block> BLOCK_REGISTRY = new ArrayList<>();
    public static final List<Item> ITEM_REGISTRY = new ArrayList<>();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        for (Block block : BLOCK_REGISTRY) {
            event.getRegistry().register(block);
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Item item: ITEM_REGISTRY) {
            event.getRegistry().register(item);
        }

        for (Block block : BLOCK_REGISTRY) {
            ItemBlock itemBlock = new ItemBlock(block);
            itemBlock.setRegistryName(block.getRegistryName());
            event.getRegistry().register(itemBlock);
        }
    }

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileDryingRack.class, ModBlocks.DRYING_RACK.getRegistryName());
        GameRegistry.registerTileEntity(TileStill.class, ModBlocks.STILL.getRegistryName());
        GameRegistry.registerTileEntity(TileTanningTub.class, ModBlocks.TANNING_TUB.getRegistryName());
    }
}
