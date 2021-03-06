package quarris.meretrifles.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.blocks.tiles.TileDryingRack;
import quarris.meretrifles.helper.BlockRegistryObject;

import javax.annotation.Nullable;

public class BlockDryingRack extends BlockContainer implements ITileEntityProvider {

    private final AxisAlignedBB SHAPE_NS = new AxisAlignedBB(0, 0, 7 / 16d, 1, 1, 9 / 16d);
    private final AxisAlignedBB SHAPE_WE = new AxisAlignedBB(7 / 16d, 0, 0, 9 / 16d, 1, 1);
    private final AxisAlignedBB SHAPE_WALL_N = new AxisAlignedBB(0, 0, 0, 1, 1, 2 / 16d);
    private final AxisAlignedBB SHAPE_WALL_S = new AxisAlignedBB(0, 0, 14 / 16f, 1, 1, 1);
    private final AxisAlignedBB SHAPE_WALL_W = new AxisAlignedBB(0, 0, 0, 2 / 16d, 1, 1);
    private final AxisAlignedBB SHAPE_WALL_E = new AxisAlignedBB(14 / 16d, 0, 0, 1, 1, 1);

    private final AxisAlignedBB[] SHAPE_FROM_META = new AxisAlignedBB[]{
            SHAPE_NS,
            SHAPE_WE,
            SHAPE_NS,
            SHAPE_WE,
            SHAPE_WALL_N,
            SHAPE_WALL_E,
            SHAPE_WALL_S,
            SHAPE_WALL_W
    };

    public static final PropertyBool ON_WALL = PropertyBool.create("on_wall");
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockDryingRack() {
        super(Material.WOOD);
        BlockRegistryObject.create(this)
                .name("drying_rack")
                .creativeTab(MereTrifles.creativeTab).soundType(SoundType.WOOD)
                .harvestLevel("axe", 0).hardness(1).resistance(5)
                .register();
    }

    public boolean isOnWall(IBlockState state) {
        return state.getValue(ON_WALL);
    }

    public EnumFacing getFacing(IBlockState state) {
        return state.getValue(FACING);
    }

    public boolean canStay(World world, BlockPos pos, IBlockState state) {
        if (this.isOnWall(state)) {
            EnumFacing facing = this.getFacing(state);
            BlockPos wallPos = pos.offset(facing.getOpposite());
            IBlockState wall = world.getBlockState(wallPos);
            return wall.isSideSolid(world, wallPos, facing);
        } else {
            IBlockState floor = world.getBlockState(pos.down());
            return floor.isSideSolid(world, pos.down(), EnumFacing.UP);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileDryingRack) {
            TileDryingRack rack = (TileDryingRack) tile;
            ItemStackHandler inventory = rack.getInventory();
            ItemStack heldItem = player.getHeldItem(hand);
            if (inventory.getStackInSlot(0).isEmpty()) {
                inventory.setStackInSlot(0, heldItem.splitStack(1));
                //player.setHeldItem(hand, inventory.insertItem(0, heldItem, false));
            } else {
                ItemStack extracted = inventory.getStackInSlot(0);
                inventory.setStackInSlot(0, ItemStack.EMPTY);
                player.addItemStackToInventory(extracted);
            }
            return true;
        }
        return false;
    }


    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDryingRack) {
            TileDryingRack rack = (TileDryingRack) tile;
            ItemStack item = rack.getInventory().getStackInSlot(0);
            if (!item.isEmpty()) {
                InventoryHelper.spawnItemStack(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return side != EnumFacing.DOWN;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canStay(worldIn, pos, state)) {
            worldIn.setBlockToAir(pos);
            this.dropBlockAsItem(worldIn, pos, state, 0);
        }
    }

    public IBlockState getStateFromProps(EnumFacing horizontalFacing, boolean onWall) {
        return this.getDefaultState().withProperty(FACING, horizontalFacing).withProperty(ON_WALL, onWall);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ON_WALL, FACING);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if (facing.getAxis().isVertical()) {
            EnumFacing facingPlacement = placer.getAdjustedHorizontalFacing().getOpposite();
            return this.getStateFromProps(facingPlacement, false);
        } else {
            return this.getStateFromProps(facing, true);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE_FROM_META[this.getMetaFromState(state)];
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getHorizontal(meta % 4);
        boolean onWall = meta / 4 == 1;
        return this.getStateFromProps(facing, onWall);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getHorizontalIndex();
        if (state.getValue(ON_WALL)) {
            meta += 4;
        }
        return meta;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDryingRack();
    }
}
