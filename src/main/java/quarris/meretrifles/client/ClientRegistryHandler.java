package quarris.meretrifles.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.registry.RegistryHandler;
import quarris.meretrifles.blocks.tiles.TileDryingRack;
import quarris.meretrifles.client.renderer.TileRendererDryingRack;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MereTrifles.MODID)
public class ClientRegistryHandler {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Block block : RegistryHandler.BLOCK_REGISTRY) {
            registerModel(Item.getItemFromBlock(block), 0, "inventory");
        }

        // ITEMS
        for (Item item : RegistryHandler.ITEM_REGISTRY) {
            registerModel(item, 0, "inventory");
        }

        ClientRegistry.bindTileEntitySpecialRenderer(TileDryingRack.class, new TileRendererDryingRack());
    }

    public static void registerModel(Item item, int meta, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
    }

}
