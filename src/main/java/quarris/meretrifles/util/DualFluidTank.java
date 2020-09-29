package quarris.meretrifles.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class DualFluidTank implements IFluidHandler {

    private IFluidTankProperties[] tankProperties;
    private final FluidTank inputTank;
    private final FluidTank outputTank;

    public DualFluidTank(int capacity) {
        this.inputTank = new FluidTank(capacity) {

            @Override
            protected void onContentsChanged() {
                DualFluidTank.this.onContentsChanged();
            }

        };
        this.outputTank = new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                DualFluidTank.this.onContentsChanged();
            }

        };
        this.outputTank.setCanFill(false);
    }

    public FluidTank getInput() {
        return this.inputTank;
    }

    public FluidTank getOutput() {
        return this.outputTank;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        if (this.tankProperties == null) {
            this.tankProperties = new IFluidTankProperties[]{new FluidTankPropertiesWrapper(this.inputTank), new FluidTankPropertiesWrapper(this.outputTank)};
        }

        return this.tankProperties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return this.inputTank.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        FluidStack result = this.outputTank.drain(resource, doDrain);
        if (result == null) {
            result = this.inputTank.drain(resource, doDrain);
        }

        return result;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack result = this.outputTank.drain(maxDrain, doDrain);
        if (result == null) {
            result = this.inputTank.drain(maxDrain, doDrain);
        }

        return result;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("InputTank", this.inputTank.writeToNBT(new NBTTagCompound()));
        nbt.setTag("OutputTank", this.outputTank.writeToNBT(new NBTTagCompound()));
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.inputTank.readFromNBT(nbt.getCompoundTag("InputTank"));
        this.outputTank.readFromNBT(nbt.getCompoundTag("OutputTank"));
    }

    public void onContentsChanged() {

    }
}
