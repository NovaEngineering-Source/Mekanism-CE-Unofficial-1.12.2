package mekanism.client.gui.filter;

import mekanism.api.Coord4D;
import mekanism.client.gui.button.GuiDisableableButton;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.GuiPlayerSlot;
import mekanism.client.gui.element.GuiSlot;
import mekanism.common.Mekanism;
import mekanism.common.OreDictCache;
import mekanism.common.content.miner.MOreDictFilter;
import mekanism.common.network.PacketDigitalMinerGui.DigitalMinerGuiMessage;
import mekanism.common.network.PacketDigitalMinerGui.MinerGuiPacket;
import mekanism.common.tile.TileEntityDigitalMiner;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMOreDictFilter extends GuiOreDictFilter<MOreDictFilter, TileEntityDigitalMiner> {

    public GuiMOreDictFilter(EntityPlayer player, TileEntityDigitalMiner tile, int index) {
        super(player, tile);
        origFilter = (MOreDictFilter) tileEntity.filters.get(index);
        filter = ((MOreDictFilter) tileEntity.filters.get(index)).clone();
        updateStackList(filter.getOreDictName());
        addGuiElement(new GuiInnerScreen(this, getGuiLocation(), 34, 19, 109, 41));
        addGuiElement(new GuiSlot(GuiSlot.SlotType.NORMAL, this, getGuiLocation(), 11, 18));
        addGuiElement(new GuiSlot(GuiSlot.SlotType.NORMAL, this, getGuiLocation(), 148, 18));
        addGuiElement(new GuiPlayerSlot(this, getGuiLocation()));
    }

    public GuiMOreDictFilter(EntityPlayer player, TileEntityDigitalMiner tile) {
        super(player, tile);
        isNew = true;
        filter = new MOreDictFilter();
        addGuiElement(new GuiInnerScreen(this, getGuiLocation(), 34, 19, 109, 41));
        addGuiElement(new GuiSlot(GuiSlot.SlotType.NORMAL, this, getGuiLocation(), 11, 18));
        addGuiElement(new GuiSlot(GuiSlot.SlotType.NORMAL, this, getGuiLocation(), 148, 18));
        addGuiElement(new GuiPlayerSlot(this, getGuiLocation()));
    }

    @Override
    protected void addButtons() {
        buttonList.add(saveButton = new GuiDisableableButton(0, guiLeft + 27, guiTop + 62, 60, 20, LangUtils.localize("gui.save")));
        buttonList.add(deleteButton = new GuiDisableableButton(1, guiLeft + 89, guiTop + 62, 60, 20, LangUtils.localize("gui.delete")));
        buttonList.add(backButton = new GuiDisableableButton(2, guiLeft + 5, guiTop + 5, 11, 11).with(GuiDisableableButton.ImageOverlay.SMALL_BACK));
        buttonList.add(replaceButton = new GuiDisableableButton(3, guiLeft + 148, guiTop + 45, 14, 14).with(GuiDisableableButton.ImageOverlay.EXCLAMATION));
        buttonList.add(checkboxButton = new GuiDisableableButton(4, guiLeft + 130, guiTop + 47, 11, 11).with(GuiDisableableButton.ImageOverlay.CHECKMARK_DIGITAL));
    }

    private void updateEnabledButtons() {
        checkboxButton.enabled = !text.getText().isEmpty();
    }


    @Override
    public void updateScreen() {
        super.updateScreen();
        updateEnabledButtons();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.SWITCH, "switch_icon.png"));
        drawTexturedModalRect(guiLeft + 38, guiTop + 48, 43, 0, 4, 7);
    }


    @Override
    protected void sendPacketToServer(int guiID) {
        Mekanism.packetHandler.sendToServer(new DigitalMinerGuiMessage(MinerGuiPacket.SERVER, Coord4D.get(tileEntity), guiID, 0, 0));
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "Null.png");
    }

    @Override
    protected void updateStackList(String oreName) {
        iterStacks = OreDictCache.getOreDictStacks(oreName, true);
        stackSwitch = 0;
        stackIndex = -1;
    }
}