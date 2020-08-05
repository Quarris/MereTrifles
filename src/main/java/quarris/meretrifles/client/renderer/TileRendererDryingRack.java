package quarris.meretrifles.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.blocks.BlockDryingRack;
import quarris.meretrifles.blocks.tiles.TileDryingRack;

@SideOnly(Side.CLIENT)
public class TileRendererDryingRack extends TileEntitySpecialRenderer<TileDryingRack> {

    private static final ResourceLocation STRING_TEX = MereTrifles.createRes("textures/blocks/drying_rack_string.png");
    private ModelRackString model = new ModelRackString();

    @Override
    public void render(TileDryingRack te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        ItemStack item = te.getInventory().getStackInSlot(0);

        if (!item.isEmpty()) {
            IBlockState state = this.getWorld().getBlockState(te.getPos());
            EnumFacing facing = state.getValue(BlockDryingRack.FACING);
            boolean onWall = state.getValue(BlockDryingRack.ON_WALL);
            GlStateManager.pushMatrix(); {
                GlStateManager.translate(x + 0.5, y + 0.5, z + (onWall ? 0.94 : 0.5));
                GlStateManager.rotate(facing.getHorizontalAngle(), 0, 1, 0);
                GlStateManager.pushMatrix(); {
                    GlStateManager.translate(-1/32f, -1.465, 0);
                    GlStateManager.scale(1, 1, 0.69d);
                    this.bindTexture(STRING_TEX);
                    this.model.render(1 / 16f);
                }
                GlStateManager.popMatrix();
                GlStateManager.scale(0.7d, 0.7d, 0.7d);
                Minecraft.getMinecraft().getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.FIXED);
            }
            GlStateManager.popMatrix();
        }
    }

    public class ModelRackString extends ModelBase {

        private ModelRenderer leftString;
        private ModelRenderer rightString;

        public ModelRackString() {
            this.textureWidth = 16;
            this.textureHeight = 16;

            this.leftString = new ModelRenderer(this);
            this.leftString.setRotationPoint(0.5F, 23.5F, 0.0F);
            this.setRotationAngle(this.leftString, 0.0F, 0.0F, 0.6857F);
            this.leftString.cubeList.add(new ModelBox(this.leftString, 0, 0, -8.5F, -0.5F, -0.5F, 17, 1, 1, 0.0F, false));

            this.rightString = new ModelRenderer(this);
            this.rightString.setRotationPoint(0.5F, 23.5F, 0.0F);
            this.setRotationAngle(this.rightString, 0.0F, 0.0F, -0.6857F);
            this.rightString.cubeList.add(new ModelBox(this.rightString, 0, 0, -8.5F, -0.5F, -0.5F, 17, 1, 1, 0.0F, false));
        }

        public void render(float scale) {
            this.leftString.render(scale);
            this.rightString.render(scale);
        }

        public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.rotateAngleX = x;
            modelRenderer.rotateAngleY = y;
            modelRenderer.rotateAngleZ = z;
        }
    }
}
