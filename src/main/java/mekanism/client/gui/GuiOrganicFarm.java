package mekanism.client.gui;

import mekanism.common.recipe.machines.FarmRecipe;
import mekanism.common.tile.prefab.TileEntityFarmMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiOrganicFarm extends GuiFarmMachine<FarmRecipe> {

    public GuiOrganicFarm(InventoryPlayer inventory, TileEntityFarmMachine<FarmRecipe> tile) {
        super(inventory, tile);
    }

}