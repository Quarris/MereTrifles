package quarris.meretrifles.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.ModGuiHandler;
import quarris.meretrifles.blocks.tiles.TileStill;
import quarris.meretrifles.helper.BlockRegistryObject;

import javax.annotation.Nullable;

public class BlockStill extends BlockContainer implements ITileEntityProvider {

    public BlockStill() {
        super(Material.IRON, MapColor.IRON);
        BlockRegistryObject.create(this)
                .name("still")
                .harvestLevel("pickaxe", 0).resistance(4).hardness(2)
                .soundType(SoundType.STONE).creativeTab(MereTrifles.creativeTab)
                .register();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!FluidUtil.interactWithFluidHandler(player, hand, world, pos, facing)) {
            player.openGui(MereTrifles.MODID, ModGuiHandler.GuiId.STILL.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileStill();
    }
}
