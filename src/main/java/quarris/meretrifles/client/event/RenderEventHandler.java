package quarris.meretrifles.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.helper.PlayerHelper;
import quarris.meretrifles.items.ItemSextant;
import quarris.meretrifles.items.ModItems;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MereTrifles.MODID)
public class RenderEventHandler {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static FontRenderer font = mc.fontRenderer;

    @SubscribeEvent
    public static void renderSextantOverlay(RenderGameOverlayEvent.Text.Pre event) {
        EntityPlayerSP player = mc.player;
        if (PlayerHelper.isPlayerHolding(player, ModItems.SEXTANT) && ItemSextant.isSkyClear(player.world, player.getPosition())) {
            double sunAngle = player.world.getCelestialAngle(0) * Math.PI * 2;
            double sunHeight = Math.cos(sunAngle);
            double moonAngle = sunAngle + Math.PI;
            double moonHeight = Math.cos(moonAngle);
            Vec3d vec3d1 = player.getLook(event.getPartialTicks());
            Vec3d sunDir = new Vec3d(-Math.sin(sunAngle), sunHeight, 0);
            Vec3d moonDir = new Vec3d(-Math.sin(moonAngle), moonHeight, 0);
            if (sunHeight >= 0 && sunDir.dotProduct(vec3d1) > 0.996 || moonHeight >= 0 && moonDir.dotProduct(vec3d1) > 0.996) {
                BlockPos pos = player.getPosition();
                String coords = "(" + pos.getX() + ", " + pos.getZ() + ")";
                ScaledResolution resolution = new ScaledResolution(mc);
                font.drawString(coords, (resolution.getScaledWidth() - font.getStringWidth(coords)) / 2f, resolution.getScaledHeight() / 2 + font.FONT_HEIGHT + 10, 0xFFFFFFFF, true);
            }
        }
    }

}
