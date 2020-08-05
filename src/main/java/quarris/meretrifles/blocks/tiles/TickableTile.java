package quarris.meretrifles.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public abstract class TickableTile extends TileBase implements ITickable {

    public long lastTicked;

    public TickableTile() {
    }

    @Override
    public void update() {
        if (this.lastTicked == 0)
            this.lastTicked = this.world.getTotalWorldTime();

        long time = this.world.getTotalWorldTime();
        long elapsed = time - this.lastTicked;
        if (elapsed > 0) {
            this.update(elapsed);
        } else if (elapsed == 0) {
            // If a mod calls the update method multiple times a tick
            // to speed up the tile it will make sure it works
            this.update(1);
        }
        this.lastTicked = time;
    }

    public abstract void update(long elapsed);

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setLong("LastTicked", this.lastTicked);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.lastTicked = compound.getLong("LastTicked");
    }
}
