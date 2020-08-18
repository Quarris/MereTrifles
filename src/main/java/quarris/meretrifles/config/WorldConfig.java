package quarris.meretrifles.config;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fluids.Fluid;
import quarris.meretrifles.MereTrifles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldConfig {

    private static WorldConfig INSTANCE;

    @Config.Name("Block Fluid Placements")
    public String[] rawBlockFluidPlacements = new String[]{};

    @Config.Name("Infinite Water Biomes")
    public String[] rawInfiniteWaterBiomes = new String[]{};

    @Config.Name("Blacklist Infinite Water Biomes")
    public boolean infiniteWaterBlacklist = true;

    private Set<ResourceLocation> blockFluidPlacements = new HashSet<>();

    private Set<ResourceLocation> infiniteWaterBiomes = new HashSet<>();

    public WorldConfig() {
        INSTANCE = this;
    }

    public void onConfigsChanged() {
        MereTrifles.LOGGER.info("Updating World Configs for {}", MereTrifles.NAME);
        this.blockFluidPlacements = Arrays.stream(this.rawBlockFluidPlacements).map(ResourceLocation::new).collect(Collectors.toSet());
        this.infiniteWaterBiomes = Arrays.stream(this.rawInfiniteWaterBiomes).map(ResourceLocation::new).collect(Collectors.toSet());
    }

    public static boolean preventFluidPlacement(Fluid fluid) {
        return INSTANCE.blockFluidPlacements.contains(fluid.getBlock().getRegistryName());
    }

    public static boolean allowWaterSourceCreation(IBlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        if (block != Blocks.WATER && block != Blocks.FLOWING_WATER) {
            return false;
        }

        if (INSTANCE.infiniteWaterBiomes.isEmpty()) {
            return INSTANCE.infiniteWaterBlacklist;
        }


        Biome biome = world.getBiome(pos);
        return INSTANCE.infiniteWaterBlacklist != INSTANCE.infiniteWaterBiomes.contains(biome.getRegistryName());
    }


}
