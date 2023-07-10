package mekanism.generators.client.gui;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.*;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismFluids;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.common.inventory.container.ContainerBioGenerator;
import mekanism.generators.common.tile.TileEntityBioGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiBioGenerator extends GuiMekanismTile<TileEntityBioGenerator> {

    public GuiBioGenerator(InventoryPlayer inventory, TileEntityBioGenerator tile) {
        super(tile, new ContainerBioGenerator(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource));
        addGuiElement(new GuiEnergyInfo(() -> Arrays.asList(
                LangUtils.localize("gui.producing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getActive() ? MekanismConfig.current().generators.bioGeneration.val() : 0) + "/t",
                LangUtils.localize("gui.maxOutput") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxOutput()) + "/t"), this, resource));
        addGuiElement(new GuiPowerBar(this, tileEntity, resource, 164, 15));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 16, 34));
        addGuiElement(new GuiSlot(SlotType.POWER, this, resource, 142, 34).with(SlotOverlay.POWER));
        addGuiElement(new GuiBlackScreenframe(GuiBlackScreenframe.BlackScreen.BIO_EVAPORATION, this, resource, 48, 23));
        addGuiElement(new GuiBlack(this, resource, 49, 24, 78, 38));
        addGuiElement(new GuiPlayerSlot(this, getGuiLocation()));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        fontRenderer.drawString(MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()), 51, 26, 0x00CD00);
        fontRenderer.drawString(LangUtils.localize("gui.bioGenerator.bioFuel") + ": " + tileEntity.bioFuelSlot.fluidStored, 51, 35, 0x00CD00);
        fontRenderer.drawString(LangUtils.localize("gui.out") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxOutput()) + "/t", 51, 44, 0x00CD00);
        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;
        if (xAxis >= 7 && xAxis <= 10 && yAxis >= 17 && yAxis <= 68) {
            List<String> info = new ArrayList<>();
            info.add(tileEntity.bioFuelSlot.fluidStored > 0 ? LangUtils.localize("gui.bioGenerator.bioFuel") + ":" + tileEntity.bioFuelSlot.fluidStored : LangUtils.localize("gui.empty"));
            displayTooltips(info, xAxis, yAxis);
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_ELEMENT, "GuiPowerBar.png"));
        drawTexturedModalRect(guiLeft + 6, guiTop + 16, 0, 1, 6, 54);
        if (tileEntity.getScaledFuelLevel(52) > 0) {
            MekanismRenderer.color(MekanismFluids.Biofuel);
            int displayInt = tileEntity.getScaledFuelLevel(52);
            displayGauge(7, 17 + 52 - displayInt, 4, displayInt, MekanismFluids.Biofuel.getSprite());
            MekanismRenderer.resetColor();
        }
    }

    public void displayGauge(int xPos, int yPos, int sizeX, int sizeY, TextureAtlasSprite icon) {
        if (icon != null) {
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            drawTexturedModalRect(guiLeft + xPos, guiTop + yPos, icon, sizeX, sizeY);
        }
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlankIcon.png");
    }
}