package quarris.meretrifles.blocks.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import quarris.meretrifles.recipe.RecipeStill;
import quarris.meretrifles.util.DualFluidTank;

import javax.annotation.Nullable;

public class TileStill extends TickableTile {

    private final ItemStackHandler inventory;
    private final DualFluidTank internalTanks;

    private int workTicks;
    private int fuelTicks;
    private int maxFuelTicks;
    private boolean isActive;
    public RecipeStill recipe;

    public TileStill() {
        this.inventory = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                TileStill.this.markDirty();
            }
        };
        this.internalTanks = new DualFluidTank(10 * Fluid.BUCKET_VOLUME) {
            @Override
            public void onContentsChanged() {
                TileStill.this.markDirty();
            }
        };
    }

    @Override
    public void update(int elapsed) {
        if (this.world.isRemote) {
            if (this.recipe == null) {
                this.recipe = RecipeStill.fromInput(this.world, this.pos, this.getInputFluid());
            }

            this.tickTime(elapsed);
            return;
        }

        while (elapsed > 0) {
            elapsed = this.operate(elapsed);
        }
    }

    private int operate(int ticks) {
        // Check if the recipe is valid
        if (this.recipe == null) {
            this.workTicks = 0;
            this.setRecipe(RecipeStill.fromInput(this.world, this.pos, this.getInputFluid()));
            if (this.recipe == null) {
                this.fuelTicks -= ticks;
                if (this.fuelTicks < 0) {
                    this.fuelTicks = 0;
                }
                return 0;
            }
            this.sendToClients();
        }

        // Can a recipe continue working? Enough Input, Enough space for Output?
        if (this.recipe.canWork(this.world, this.pos, this.getInputFluid())) {
            if (this.getOutputFluid() == null || this.getOutputFluid().isFluidEqual(this.recipe.getRecipeOuput())) {
                int workTime = Math.min(this.recipe.getWorkTime(), ticks);

                this.fuelTicks -= workTime;
                while (this.fuelTicks < 0) {
                    if (this.getFuelItem().isEmpty()) {
                        this.fuelTicks = 0;
                        this.workTicks = 0;
                        return 0;
                    }
                    this.maxFuelTicks = this.burnFuel();
                    this.fuelTicks += this.maxFuelTicks;
                    this.sendToClients();
                }

                if (!this.isActive) {
                    this.isActive = true;
                    this.sendToClients();
                }

                this.workTicks += workTime;
                if (this.workTicks >= this.recipe.getWorkTime()) {
                    // we're done with an operation
                    this.getOutputTank().fillInternal(this.recipe.createOutput(this.recipe.getRecipeOuput().amount), true);
                    this.getInputTank().drainInternal(this.recipe.getRecipeInput(), true);
                    this.workTicks -= this.recipe.getWorkTime();
                    this.setRecipe(null);
                    this.sendToClients();
                }

                return ticks - workTime;
            }
        }

        this.setRecipe(null);
        this.workTicks = 0;
        this.fuelTicks -= ticks;
        if (this.fuelTicks < 0)
            this.fuelTicks = 0;
        this.sendToClients();
        return 0;
    }

    public int getFuelBurnTime() {
        return TileEntityFurnace.getItemBurnTime(this.getFuelItem());
    }

    public void setRecipe(RecipeStill recipe) {
        this.recipe = recipe;
        if (recipe == null) {
            this.isActive = false;
            this.sendToClients();
        }
    }

    public int burnFuel() {
        ItemStack fuelItem = this.getFuelItem();
        int fuel = this.getFuelBurnTime();
        if (fuel > 0) {
            fuelItem.shrink(1);
        }

        return fuel;
    }

    private void tickTime(int amount) {
        if (this.isActive()) {
            this.workTicks += amount;
            if (this.workTicks > this.recipe.getWorkTime()) {
                this.workTicks = 0;
            }
        }

        if (this.isBurning()) {
            this.fuelTicks -= amount;
            if (this.fuelTicks < 0) {
                this.fuelTicks = 0;
            }
        }
    }

    public ItemStack getFuelItem() {
        return this.inventory.getStackInSlot(0);
    }

    public FluidStack getInputFluid() {
        return this.getInputTank().getFluid();
    }

    public FluidStack getOutputFluid() {
        return this.getOutputTank().getFluid();
    }

    public FluidTank getInputTank() {
        return this.internalTanks.getInput();
    }

    public FluidTank getOutputTank() {
        return this.internalTanks.getOutput();
    }

    @SideOnly(Side.CLIENT)
    public float getBurnProgress() {
        return 1 - this.fuelTicks / (float) this.maxFuelTicks;
    }

    @SideOnly(Side.CLIENT)
    public float getWorkProgress() {
        return this.workTicks / (float) this.recipe.getWorkTime();
    }

    public boolean isBurning() {
        return this.fuelTicks > 0;
    }

    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("FuelTicks", this.fuelTicks);
        compound.setInteger("MaxFuelTicks", this.maxFuelTicks);
        compound.setInteger("WorkTicks", this.workTicks);
        compound.setBoolean("IsActive", this.isActive);
        compound.setTag("Inventory", this.inventory.serializeNBT());
        this.internalTanks.writeToNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.fuelTicks = compound.getInteger("FuelTicks");
        this.maxFuelTicks = compound.getInteger("MaxFuelTicks");
        this.workTicks = compound.getInteger("WorkTicks");
        this.isActive = compound.getBoolean("IsActive");
        this.internalTanks.readFromNBT(compound);
        this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ||
                capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
                super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this.internalTanks;
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory;
        }

        return super.getCapability(capability, facing);
    }
}
