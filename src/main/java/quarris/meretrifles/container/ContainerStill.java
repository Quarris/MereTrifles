package quarris.meretrifles.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import quarris.meretrifles.blocks.tiles.TileStill;
import quarris.meretrifles.helper.ContainerHelper;
import quarris.meretrifles.util.SlotFluidItemHandler;
import quarris.meretrifles.util.SlotFuelItemHandler;

public class ContainerStill extends Container {

    private EntityPlayer player;
    private World world;
    private BlockPos pos;
    public TileStill tile;

    public ContainerStill(EntityPlayer player, World world, BlockPos pos) {
        this.player = player;
        this.world = world;
        this.pos = pos;

        ContainerHelper.addPlayerSlots(player.inventory, 8, 84, this::addSlotToContainer);
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileStill && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            this.tile = (TileStill) tile;
            IItemHandler tileInv = world.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            this.addSlotToContainer(new SlotFuelItemHandler(tileInv, 0, 80, 53));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn.getDistanceSqToCenter(this.pos) <= 25;
    }
}
