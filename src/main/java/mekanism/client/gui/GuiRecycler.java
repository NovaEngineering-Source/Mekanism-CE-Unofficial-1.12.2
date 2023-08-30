package mekanism.client.gui;

import mekanism.common.recipe.machines.RecyclerRecipe;
import mekanism.common.tile.prefab.TileEntityChanceMachine2;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRecycler extends GuiChanceMachine2<RecyclerRecipe> {

    public GuiRecycler(InventoryPlayer inventory, TileEntityChanceMachine2<RecyclerRecipe> tile) {
        super(inventory, tile);
    }

}
