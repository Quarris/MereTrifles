package quarris.meretrifles.helper;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.RegistryHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlockRegistryObject {

    private static final Method SET_SOUND_TYPE = ObfuscationReflectionHelper.findMethod(Block.class, "func_149672_a", Block.class, SoundType.class);

    public static BlockRegistryObject create(Block block) {
        return new BlockRegistryObject(block);
    }

    public final Block block;

    private BlockRegistryObject(Block block) {
        this.block = block;
    }

    public BlockRegistryObject name(String name) {
        this.block.setRegistryName(MereTrifles.createRes(name));
        this.block.setUnlocalizedName(MereTrifles.MODID + "." + name);
        return this;
    }

    public BlockRegistryObject creativeTab(CreativeTabs tab) {
        this.block.setCreativeTab(tab);
        return this;
    }

    public BlockRegistryObject soundType(SoundType sound) {
        try {
            SET_SOUND_TYPE.invoke(this.block, sound);
        } catch (IllegalAccessException | InvocationTargetException e) {
            MereTrifles.logger.warn("Could not set sound for " + this.block);
        }
        return this;
    }

    public BlockRegistryObject hardness(float hardness) {
        this.block.setHardness(hardness);
        return this;
    }

    public BlockRegistryObject resistance(float resistance) {
        this.block.setResistance(resistance);
        return this;
    }

    public BlockRegistryObject harvestLevel(String toolType, int level) {
        this.block.setHarvestLevel(toolType, level);
        return this;
    }

    public void register() {
        RegistryHandler.BLOCK_REGISTRY.add(this.block);
    }
}
