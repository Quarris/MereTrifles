package quarris.meretrifles.blocks.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import quarris.meretrifles.api.recipe.RecipeDryingRack;

import javax.annotation.Nonnull;

public class TileDryingRack extends TickableTile {

    private ItemStackHandler inventory;

    private RecipeDryingRack recipe;
    private int ticks;

    public TileDryingRack() {
        this.inventory = new ItemStackHandler(1) {
            @Override
            protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
                return 1;
            }

            @Override
            protected void onContentsChanged(int slot) {
                TileDryingRack.this.sendToClients();
            }
        };
    }

    @Override
    public void update(long elapsed) {
        if (this.world.isRemote) {
            if (this.ticks > 0)
                this.tickTime(elapsed);
            return;
        }

        ItemStack slot = this.getInventory().getStackInSlot(0);

        if (slot.isEmpty()) {
            if (this.recipe != null) {
                this.recipe = null;
                this.ticks = 0;
            }
            return;
        }

        if (this.recipe == null) {
            this.recipe = RecipeDryingRack.fromInput(slot);
            this.ticks = this.recipe.time;
        }

        this.tickTime(elapsed);

        if (this.ticks == 0) {
            this.getInventory().setStackInSlot(0, this.recipe.output.copy());
            this.recipe = null;
            this.sendToClients();
        }
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    private void tickTime(long amount) {
        this.ticks -= amount;
        if (this.ticks < 0)
            this.ticks = 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setTag("Inventory", this.getInventory().serializeNBT());
        compound.setInteger("TicksUsed", this.ticks);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.getInventory().deserializeNBT(compound.getCompoundTag("Inventory"));
        this.ticks = compound.getInteger("TicksUsed");
    }
}
