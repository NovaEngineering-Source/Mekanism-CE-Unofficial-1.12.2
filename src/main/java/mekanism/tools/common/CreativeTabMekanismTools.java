package mekanism.tools.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;


public class CreativeTabMekanismTools extends CreativeTabs {

    public CreativeTabMekanismTools() {
        super("tabMekanismTools");
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ToolsItem.STEEL_PAXEL.getItem());
    }

}
