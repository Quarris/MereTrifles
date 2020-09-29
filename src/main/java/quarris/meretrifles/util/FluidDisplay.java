package quarris.meretrifles.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.NumberFormat;
import java.util.Collections;

// Copied and modified from ActuallyAdditions with permission from Ellpeck.
// https://github.com/Ellpeck/ActuallyAdditions/blob/master/src/main/java/de/ellpeck/actuallyadditions/mod/inventory/gui/FluidDisplay.java

@SideOnly(Side.CLIENT)
public class FluidDisplay {

    private FluidTank fluidReference;
    private Fluid oldFluid;

    private int x;
    private int y;
    private int width;
    private int height;

    private ResourceLocation resLoc;

    public FluidDisplay(int x, int y, int width, int height, FluidTank fluidReference) {
        this.setData(x, y, width, height, fluidReference);
    }

    public void setData(int x, int y, int width, int height, FluidTank fluidReference) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fluidReference = fluidReference;
    }

    public void setData(GuiContainer parent, int x, int y, int width, int height, FluidTank fluidReference) {
        this.setData(parent.getGuiLeft() + x, parent.getGuiTop() + y, width, height, fluidReference);
    }

    public void draw() {
        Minecraft mc = Minecraft.getMinecraft();

        FluidStack stack = this.fluidReference.getFluid();
        Fluid fluid = stack == null ? null : stack.getFluid();

        if (this.resLoc == null || this.oldFluid != fluid) {
            this.oldFluid = fluid;

            if (fluid != null && fluid.getStill() != null) {
                this.resLoc = new ResourceLocation(fluid.getStill().getResourceDomain(), "textures/" + fluid.getStill().getResourcePath() + ".png");
            }
        }

        if (stack != null && fluid != null && this.resLoc != null) {
            mc.getTextureManager().bindTexture(this.resLoc);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            float fluidPerc = this.fluidReference.getFluidAmount() / (float) this.fluidReference.getCapacity();
            Gui.drawModalRectWithCustomSizedTexture(this.x, this.y + this.height - (int)(fluidPerc * this.height), 36, 172, this.width, (int) (fluidPerc * this.height), 16, 512);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
    }

    public void drawOverlay(int mouseX, int mouseY) {
        if (mouseX >= this.x - 1 && mouseY >= this.y - 1 && mouseX < this.x + this.width + 2 && mouseY < this.y + this.height + 2) {
            Minecraft mc = Minecraft.getMinecraft();
            GuiUtils.drawHoveringText(Collections.singletonList(this.getOverlayText()), mouseX, mouseY, mc.displayWidth, mc.displayHeight, -1, mc.fontRenderer);
        }
    }

    private String getOverlayText() {
        NumberFormat format = NumberFormat.getInstance();
        FluidStack stack = this.fluidReference.getFluid();
        String cap = format.format(this.fluidReference.getCapacity());
        return stack == null || stack.getFluid() == null ? "0/" + cap + " mB" : format.format(this.fluidReference.getFluidAmount()) + "/" + cap + " mB " + stack.getLocalizedName();
    }
}