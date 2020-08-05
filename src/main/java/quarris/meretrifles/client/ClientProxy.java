package quarris.meretrifles.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import quarris.meretrifles.CommonProxy;

public class ClientProxy extends CommonProxy {


    @Override
    public float getSunAngle(World world) {
        return world.getCelestialAngle(Minecraft.getMinecraft().getRenderPartialTicks());
    }

    @Override
    public float getMoonAngle(World world) {
        return world.getCelestialAngle(Minecraft.getMinecraft().getRenderPartialTicks());
    }
}
