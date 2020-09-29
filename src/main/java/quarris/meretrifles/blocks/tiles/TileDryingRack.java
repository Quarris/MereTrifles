package quarris.meretrifles.blocks.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import quarris.meretrifles.recipe.RecipeDryingRack;

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
    public void update(int elapsed) {
        if (this.world.isRemote) {
            if (this.ticks > 0)
                this.tickTime(elapsed);
            return;
        }

        ItemStack slot = this.inventory.getStackInSlot(0);

        if (slot.isEmpty()) {
            if (this.recipe != null) {
                this.recipe = null;
                this.ticks = 0;
            }
            return;
        }

        if (this.recipe == null) {
            this.recipe = RecipeDryingRack.fromInput(this.world, this.pos, slot);
            if (this.recipe == null)
                return;

            this.ticks = this.recipe.getWorkTime();
        }

        if (!this.recipe.canWork(this.world, this.pos))
            return;

        this.tickTime(elapsed);

        if (this.ticks == 0) {
            this.inventory.setStackInSlot(0, this.recipe.getOutput());
            this.recipe = null;
            this.sendToClients();
        }
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    private void tickTime(int amount) {
        this.ticks -= amount;
        if (this.ticks < 0)
            this.ticks = 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setTag("Inventory", this.inventory.serializeNBT());
        compound.setInteger("TicksUsed", this.ticks);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        this.ticks = compound.getInteger("TicksUsed");
    }
}
