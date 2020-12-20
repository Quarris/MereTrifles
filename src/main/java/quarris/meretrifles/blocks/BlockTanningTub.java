package quarris.meretrifles.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.blocks.tiles.TileTanningTub;
import quarris.meretrifles.helper.BlockRegistryObject;

import javax.annotation.Nullable;

public class BlockTanningTub extends BlockContainer {

    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0, 0, 0, 1, 8 / 16f, 1);

    public BlockTanningTub() {
        super(Material.WOOD);
        BlockRegistryObject.create(this)
                .name("tanning_tub")
                .harvestLevel("axe", 0).hardness(1).resistance(5)
                .creativeTab(MereTrifles.creativeTab).soundType(SoundType.WOOD)
                .register();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTanningTub) {
            if (!FluidUtil.interactWithFluidHandler(player, hand, world, pos, facing)) {
                IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                ItemStack held = player.getHeldItem(hand);
                ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, held, false);
                if (remaining.getCount() == held.getCount()) { // No item was inserted
                    ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(0, inventory.getSlotLimit(0), false), player.inventory.currentItem);
                } else {
                    player.setHeldItem(hand, remaining);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile != null) {
            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (handler != null) {
                FluidStack stack = handler.getTankProperties()[0].getContents();
                if (stack != null) {
                    return stack.getFluid().getLuminosity(stack);
                }
            }
        }

        return super.getLightValue(state, world, pos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTanningTub();
    }
}
