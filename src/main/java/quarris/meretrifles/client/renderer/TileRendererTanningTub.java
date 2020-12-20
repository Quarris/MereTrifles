package quarris.meretrifles.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import quarris.meretrifles.blocks.tiles.TileTanningTub;

public class TileRendererTanningTub extends TileEntitySpecialRenderer<TileTanningTub> {

    @Override
    public void render(TileTanningTub te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        // Render item
        ItemStack item = te.inventory.getStackInSlot(0);
        if (item != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 1 / 16d, z + 0.5);
            GlStateManager.scale(0.6d, 0.6d, 0.6d);
            GlStateManager.rotate(90, 1, 0, 0);
            Minecraft.getMinecraft().getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        // Render fluid
        FluidStack fluid = te.tank.getFluid();
        if (fluid != null) {
            GlStateManager.pushMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.translate(x + 1 / 16d, y + 7 / 16d, z + 1 / 16d);
            GlStateManager.rotate(90, 1, 0, 0);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill().toString());
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            int col = fluid.getFluid().getColor(te.getWorld(), te.getPos());
            float r = ((col >> 16) & 0xFF) / 255f;
            float g = ((col >> 8) & 0xFF) / 255f;
            float b = ((col) & 0xFF) / 255f;
            float a = ((col >> 24) & 0xFF) / 255f;

            float size = 14 / 16f;
            float minU = sprite.getInterpolatedU(1);
            float maxU = sprite.getInterpolatedU(15);
            float minV = sprite.getInterpolatedV(1);
            float maxV = sprite.getInterpolatedV(15);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(0, size, 0).tex(minU, maxV).color(r, g, b, a).endVertex();
            bufferbuilder.pos(size, size, 0).tex(maxU, maxV).color(r, g, b, a).endVertex();
            bufferbuilder.pos(size, 0, 0).tex(maxU, minV).color(r, g, b, a).endVertex();
            bufferbuilder.pos(0, 0, 0).tex(minU, minV).color(r, g, b, a).endVertex();
            tessellator.draw();


            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            RenderHelper.enableStandardItemLighting();
        }
    }
}
