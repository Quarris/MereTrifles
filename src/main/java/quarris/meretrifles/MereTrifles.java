package quarris.meretrifles;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import quarris.meretrifles.api.MereTriflesApi;
import quarris.meretrifles.api.recipe.RecipeDryingRack;
import quarris.meretrifles.blocks.ModBlocks;
import quarris.meretrifles.client.CompassAngleProperty;
import quarris.meretrifles.items.ModItems;
import quarris.meretrifles.registry.JsonRecipeLoader;
import quarris.meretrifles.registry.RegistryHandler;

import java.io.File;

@Mod(modid = MereTrifles.MODID, name = MereTrifles.NAME, version = MereTrifles.VERSION)
public class MereTrifles {
    public static final String MODID = "meretrifles";
    public static final String NAME = "Mere Trifles";
    public static final String VERSION = "1.0";

    public static Logger LOGGER;

    @SidedProxy(clientSide = "quarris.meretrifles.client.ClientProxy", serverSide = "quarris.meretrifles.CommonProxy")
    public static CommonProxy proxy;

    public static CreativeTabs creativeTab = new CreativeTabs(MODID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.STICK);
        }
    };

    public static ResourceLocation createRes(String name) {
        return new ResourceLocation(MODID, name);
    }

    public MereTrifles() {

    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        MereTriflesApi.LOGGER = event.getModLog();
        JsonRecipeLoader.init(new File(event.getModConfigurationDirectory(), "meretrifles"));
        ModBlocks.init();
        ModItems.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        RegistryHandler.registerTileEntities();
        JsonRecipeLoader.loadRecipes();
        Items.COMPASS.addPropertyOverride(new ResourceLocation("angle"), new CompassAngleProperty());
    }
}
