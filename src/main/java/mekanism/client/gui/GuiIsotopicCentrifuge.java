package mekanism.client.gui;

import mekanism.client.gui.element.*;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiSideConfigurationTab;
import mekanism.client.gui.element.tab.GuiTransporterConfigTab;
import mekanism.client.gui.element.tab.GuiUpgradeTab;
import mekanism.common.inventory.container.ContainerIsotopicCentrifuge;
import mekanism.common.tile.TileEntityIsotopicCentrifuge;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class GuiIsotopicCentrifuge extends GuiMekanismTile<TileEntityIsotopicCentrifuge> {

    public GuiIsotopicCentrifuge(InventoryPlayer inventory, TileEntityIsotopicCentrifuge tile) {
        super(tile, new ContainerIsotopicCentrifuge(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiUpgradeTab(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiPowerBarHorizontal(this, tileEntity, resource, 115 - 2, 74));
        addGuiElement(new GuiSideConfigurationTab(this, tileEntity, resource));
        addGuiElement(new GuiTransporterConfigTab(this, 34, tileEntity, resource));
        addGuiElement(new GuiEnergyInfo(() -> {
            String usage = MekanismUtils.getEnergyDisplay(tileEntity.clientEnergyUsed);
            return Arrays.asList(LangUtils.localize("gui.using") + ": " + usage + "/t",
                    LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy()));
        }, this, resource));
        addGuiElement(new GuiGasGauge(() -> tileEntity.inputTank, GuiGauge.Type.STANDARD_RED, this, resource, 26, 13));
        addGuiElement(new GuiGasGauge(() -> tileEntity.outputTank, GuiGauge.Type.STANDARD_BLUE, this, resource, 133, 13));
        addGuiElement(new GuiSlot(SlotType.POWER, this, resource, 154, 4).with(SlotOverlay.POWER));
        addGuiElement(new GuiSlot(SlotType.INPUT, this, resource, 4, 55).with(SlotOverlay.MINUS));
        addGuiElement(new GuiSlot(SlotType.OUTPUT, this, resource, 154, 55).with(SlotOverlay.PLUS));
        addGuiElement(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getActive() ? 1 : 0;
            }
        }, ProgressBar.LARGE_RIGHT, this, resource, 62, 39));
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlankIcon.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
    }
}