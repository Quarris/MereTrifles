package quarris.meretrifles;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import quarris.meretrifles.client.gui.GuiStill;
import quarris.meretrifles.container.ContainerStill;

import javax.annotation.Nullable;

public class ModGuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return GuiId.createServerElement(ID, player, world, x, y, z);
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return GuiId.createClientElement(ID, player, world, x, y, z);
    }

    public enum GuiId {
        STILL((player, world, pos) -> new GuiStill(new ContainerStill(player, world, pos)), ContainerStill::new);

        private GuiSupplier clientGuiElement;
        private ContainerSupplier serverGuiElement;

        GuiId(GuiSupplier clientGuiElement, ContainerSupplier serverGuiElement) {
            this.clientGuiElement = clientGuiElement;
            this.serverGuiElement = serverGuiElement;
        }

        static Gui createClientElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            if (id < 0 || id >= GuiId.values().length)
                return null;

            return GuiId.values()[id].clientGuiElement.create(player, world, new BlockPos(x, y, z));
        }

        static Container createServerElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            if (id < 0 || id >= GuiId.values().length)
                return null;

            return GuiId.values()[id].serverGuiElement.create(player, world, new BlockPos(x, y, z));
        }
    }

    private interface GuiSupplier {
        Gui create(EntityPlayer player, World world, BlockPos pos);
    }

    private interface ContainerSupplier {
        Container create(EntityPlayer player, World world, BlockPos pos);
    }
}
