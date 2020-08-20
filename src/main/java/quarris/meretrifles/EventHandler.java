package quarris.meretrifles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import quarris.meretrifles.config.WorldConfig;
import quarris.meretrifles.items.ModItems;

@Mod.EventBusSubscriber(modid = MereTrifles.MODID)
public class EventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void blockFluidPlacement(FillBucketEvent event) {
        if (event.getTarget() == null || event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }

        FluidBucketWrapper fluidHandler = (FluidBucketWrapper) event.getEmptyBucket().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

        FluidStack fluid = fluidHandler.getFluid();
        if (fluid == null || fluid.getFluid() == null)
            return;

        if (WorldConfig.preventFluidPlacement(fluid.getFluid()))
            event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void blockFluidCollection(FillBucketEvent event) {

    }

    @SubscribeEvent
    public static void fluidStuff(BlockEvent.CreateFluidSourceEvent event) {
        if (WorldConfig.allowWaterSourceCreation(event.getState(), event.getWorld(), event.getPos())) {
            event.setResult(Event.Result.DENY);
        }
    }
    }
}
