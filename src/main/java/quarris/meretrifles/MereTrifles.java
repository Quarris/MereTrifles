package quarris.meretrifles;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import quarris.meretrifles.blocks.ModBlocks;
import quarris.meretrifles.client.CompassAngleProperty;
import quarris.meretrifles.config.ModConfig;
import quarris.meretrifles.items.ModItems;
import quarris.meretrifles.registry.JsonRecipeLoader;
import quarris.meretrifles.registry.RegistryHandler;

import java.io.File;

@Mod(modid = MereTrifles.MODID, name = MereTrifles.NAME, version = MereTrifles.VERSION)
public class MereTrifles {
    public static final String MODID = "meretrifles";
    public static final String NAME = "Mere Trifles";
    public static final String VERSION = "0.2";

    public static Logger LOGGER;

    @SidedProxy(clientSide = "quarris.meretrifles.client.ClientProxy", serverSide = "quarris.meretrifles.CommonProxy")
    public static CommonProxy PROXY;

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
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        ModConfig.onConfigChanged();
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

    @SubscribeEvent
    public void onConfigsChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (MODID.equals(event.getModID())) {
            ConfigManager.sync(MODID, Config.Type.INSTANCE);
            ModConfig.onConfigChanged();
        }
    }


}
