package quarris.meretrifles.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class CompassAngleProperty implements IItemPropertyGetter {

    @SideOnly(Side.CLIENT)
    double rotation;
    @SideOnly(Side.CLIENT)
    double rota;
    @SideOnly(Side.CLIENT)
    long lastUpdateTick;

    @SideOnly(Side.CLIENT)
    public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase holder) {
        if (holder == null && !stack.isOnItemFrame()) {
            return 0.0F;
        } else {
            boolean hasHolder = holder != null;
            Entity entity = hasHolder ? holder : stack.getItemFrame();

            if (world == null) {
                world = entity.world;
            }

            double northPoint;

            if (world.provider.isSurfaceWorld()) {
                double pointDir = hasHolder ? (double) entity.rotationYaw : this.getFrameRotation((EntityItemFrame) entity);
                pointDir = MathHelper.positiveModulo(pointDir / 360.0D, 1.0D);
                northPoint = -pointDir + 0.5f;
            } else {
                northPoint = Math.random();
            }

            if (hasHolder) {
                northPoint = this.wobble(world, northPoint);
            }

            return MathHelper.positiveModulo((float) northPoint, 1.0F);
        }
    }

    @SideOnly(Side.CLIENT)
    private double wobble(World worldIn, double direction) {
        if (worldIn.getTotalWorldTime() != this.lastUpdateTick) {
            this.lastUpdateTick = worldIn.getTotalWorldTime();
            double wobbleDirection = direction - this.rotation;
            wobbleDirection = MathHelper.positiveModulo(wobbleDirection + 0.5D, 1.0D) - 0.5D;
            this.rota += wobbleDirection * 0.1D;
            this.rota *= 0.8D;
            this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
        }

        return this.rotation;
    }

    @SideOnly(Side.CLIENT)
    private double getFrameRotation(EntityItemFrame p_185094_1_) {
        return MathHelper.wrapDegrees(180 + p_185094_1_.facingDirection.getHorizontalIndex() * 90);
    }
}
