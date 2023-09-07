package mekanism.generators.client.gui;

import java.util.Arrays;
import mekanism.api.EnumColor;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.*;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.inventory.container.ContainerWindGenerator;
import mekanism.generators.common.tile.TileEntityWindGenerator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiWindGenerator extends GuiMekanismTile<TileEntityWindGenerator> {

    public GuiWindGenerator(InventoryPlayer inventory, TileEntityWindGenerator tile) {
        super(tile, new ContainerWindGenerator(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource));
        addGuiElement(new GuiEnergyInfo(() -> Arrays.asList(
                LangUtils.localize("gui.producing") + ": " +
                        MekanismUtils.getEnergyDisplay(tileEntity.getActive() ? MekanismConfig.current().generators.windGenerationMin.val() * tileEntity.getCurrentMultiplier() : 0) + "/t",
                LangUtils.localize("gui.maxOutput") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxOutput()) + "/t"), this, resource));
        addGuiElement(new GuiPowerBar(this, tileEntity, resource, 164, 15));
        addGuiElement(new GuiSlot(SlotType.POWER, this, resource, 142, 34).with(SlotOverlay.POWER));
        addGuiElement(new GuiPlayerSlot(this, resource));
        addGuiElement(new GuiSlot(SlotType.STATE_HOLDER, this, resource, 18, 35));
        addGuiElement(new GuiInnerScreen(this, resource, 48, 21, 80, 44));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        fontRenderer.drawString(MekanismUtils.getEnergyDisplay(tileEntity.getEnergy(), tileEntity.getMaxEnergy()), 51, 26, 0xFF3CFE9A);
        fontRenderer.drawString(LangUtils.localize("gui.power") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getActive() ?
                MekanismConfig.current().generators.windGenerationMin.val() * tileEntity.getCurrentMultiplier() : 0) + "/t", 51, 35, 0xFF3CFE9A);
        fontRenderer.drawString(LangUtils.localize("gui.out") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxOutput()) + "/t", 51, 44, 0xFF3CFE9A);
        int size = 44;
        boolean isblacklist = tileEntity.isBlacklistDimension();
        if (!tileEntity.getActive()) {
            String info = "gui.skyBlocked";
            size += 9;
            if (isblacklist) {
                info = "gui.noWind";
            }
            if (tileEntity.controlType == IRedstoneControl.RedstoneControl.HIGH && !tileEntity.redstone && !isblacklist) {
                info = "control.high.desc";
            }
            if (tileEntity.controlType == IRedstoneControl.RedstoneControl.LOW && tileEntity.redstone && !isblacklist) {
                info = "control.low.desc";
            }
            fontRenderer.drawString(EnumColor.DARK_RED + LangUtils.localize(info), 51, size, 0x00CD00);
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.SLOT, "Slot_Icon.png"));
        drawTexturedModalRect(guiLeft + 20, guiTop + 37, tileEntity.getActive() ? 12 : 0, 88, 12, 12);
    }

}
