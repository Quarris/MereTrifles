package quarris.meretrifles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import quarris.meretrifles.items.ModItems;

@Mod.EventBusSubscriber(modid = MereTrifles.MODID)
public class EventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void doBucketEventStuff(FillBucketEvent event) {
        if (event.getTarget() == null || event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }

        FluidBucketWrapper bucketFluidHandler = (FluidBucketWrapper) event.getEmptyBucket().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

        FluidStack bucketFluid = bucketFluidHandler.getFluid();

        if (bucketFluid == null) {
            if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK) {
                World world = event.getWorld();
                IBlockState blockState = world.getBlockState(event.getTarget().getBlockPos());
                /* TODO Cancel bucket events based on fluids in the config
                if (isBlockedViaConfig(blockState)) {

                }
                * */
                event.setCanceled(true);
            }
            return;
        }


        if (event.getEmptyBucket().getItem() == Items.WATER_BUCKET) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void printSextantCoords(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            if (!player.world.isRemote && player.getHeldItemMainhand().getItem() == ModItems.SEXTANT) {
                double sunAngle = player.world.getCelestialAngle(0) * Math.PI * 2;
                double sunHeight = Math.cos(sunAngle);
                double moonAngle = sunAngle + Math.PI;
                double moonHeight = Math.cos(moonAngle);
                Vec3d vec3d1 = player.getLook(1.0F);
                Vec3d sunDir = new Vec3d(-Math.sin(sunAngle), sunHeight, 0);
                Vec3d moonDir = new Vec3d(-Math.sin(moonAngle), moonHeight, 0);
                if (player.world.canBlockSeeSky(player.getPosition())) {
                    if (sunHeight >= 0 && sunDir.dotProduct(vec3d1) > 0.996 || moonHeight >= 0 && moonDir.dotProduct(vec3d1) > 0.996) {
                        BlockPos pos = player.getPosition();
                        String coords = "(" + pos.getX() + ", " + pos.getZ() + ")";
                        // TODO Change this to display the coordinates in a render event rather than a status message
                        player.sendStatusMessage(new TextComponentString(coords), true);
                    }
                }
            }
        }
    }
}
