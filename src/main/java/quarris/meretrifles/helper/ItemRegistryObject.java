package quarris.meretrifles.helper;

import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.RegistryHandler;

import java.lang.reflect.InvocationTargetException;

public class ItemRegistryObject {

    public static ItemRegistryObject create(Item item) {
        return new ItemRegistryObject(item);
    }

    public final Item item;

    protected ItemRegistryObject(Item item) {
        this.item = item;
    }

    public ItemRegistryObject name(String name) {
        this.item.setRegistryName(MereTrifles.createRes(name));
        this.item.setUnlocalizedName(MereTrifles.MODID + "." + name);
        return this;
    }

    public ItemRegistryObject creativeTab(CreativeTabs tab) {
        this.item.setCreativeTab(tab);
        return this;
    }

    public void register() {
        RegistryHandler.ITEM_REGISTRY.add(this.item);
    }

}
