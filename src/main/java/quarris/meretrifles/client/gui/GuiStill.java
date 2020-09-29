package quarris.meretrifles.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import quarris.meretrifles.MereTrifles;
import quarris.meretrifles.container.ContainerStill;
import quarris.meretrifles.util.FluidDisplay;

public class GuiStill extends GuiContainer {

    public static final ResourceLocation GUI_TEXTURE = MereTrifles.createRes("textures/gui/still.png");

    private ContainerStill container;
    private FluidDisplay inputFluidDisplay;
    private FluidDisplay outputFluidDisplay;

    public GuiStill(ContainerStill container) {
        super(container);
        this.container = container;
        this.inputFluidDisplay = new FluidDisplay(0, 0, 0, 0, this.container.tile.getInputTank());
        this.outputFluidDisplay = new FluidDisplay(0, 0, 0, 0, this.container.tile.getOutputTank());
    }

    @Override
    public void initGui() {
        super.initGui();
        this.inputFluidDisplay.setData(this, 39, 10, 16, 59, this.container.tile.getInputTank());
        this.outputFluidDisplay.setData(this, 121, 10, 16, 59, this.container.tile.getOutputTank());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if (this.container.tile.isBurning()) {
            this.drawTexturedModalRect(this.guiLeft + 81, this.guiTop + 37 + this.burnPixels(), 176, this.burnPixels(), 14, 14 - this.burnPixels());
        }

        if (this.container.tile.isActive()) {
            this.drawTexturedModalRect(this.guiLeft + 77, this.guiTop + 19, 176, 14, this.workPixels(), 17);
        }

        this.inputFluidDisplay.draw();
        this.outputFluidDisplay.draw();
        this.inputFluidDisplay.drawOverlay(mouseX, mouseY);
        this.outputFluidDisplay.drawOverlay(mouseX, mouseY);
    }

    private int burnPixels() {
        return (int) Math.ceil(14 * this.container.tile.getBurnProgress());
    }

    private int workPixels() {
        return (int) Math.ceil(24 * this.container.tile.getWorkProgress());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
