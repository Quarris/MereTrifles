package quarris.meretrifles.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileTanningTub extends TickableTile {

    public FluidTank tank = new FluidTank(Fluid.BUCKET_VOLUME) {
        @Override
        protected void onContentsChanged() {
            TileTanningTub.this.sendToClients();
        }
    };
    public ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            TileTanningTub.this.sendToClients();
        }
    };

    @Override
    public void update(int elapsed) {

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Tank", this.tank.writeToNBT(new NBTTagCompound()));
        compound.setTag("Inventory", this.inventory.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.tank.readFromNBT(compound.getCompoundTag("Tank"));
        this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
                capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ||
                super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this.tank;
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory;
        }

        return super.getCapability(capability, facing);
    }
}
