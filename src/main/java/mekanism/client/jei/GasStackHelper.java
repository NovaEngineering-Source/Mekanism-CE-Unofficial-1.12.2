package mekanism.client.jei;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.common.MekanismBlocks;
import mekanism.common.util.ItemDataUtils;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class GasStackHelper implements IIngredientHelper<GasStack> {

    @Override
    public List<GasStack> expandSubtypes(List<GasStack> contained) {
        return contained;
    }

    @Override
    @Nullable
    public GasStack getMatch(Iterable<GasStack> ingredients, GasStack toMatch) {
        for (GasStack stack : ingredients) {
            if (toMatch.getGas() == stack.getGas()) {
                return stack;
            }
        }
        return null;
    }

    @Override
    public String getDisplayName(GasStack ingredient) {
        return ingredient.getGas().getLocalizedName();
    }


    @Override
    public String getUniqueId(GasStack ingredient) {
        return "gas:" + ingredient.getGas().getName();
    }

    @Override
    public String getWildcardId(GasStack ingredient) {
        return getUniqueId(ingredient);
    }

    @Override
    public String getModId(GasStack ingredient) {
        return ingredient.getGas().getIcon().getNamespace();
    }

    @Override
    public Iterable<Color> getColors(GasStack ingredient) {
        return Collections.emptyList();
    }

    @Override
    public String getResourceId(GasStack ingredient) {
        return ingredient.getGas().getTranslationKey();
    }


    @Override
    public ItemStack getCheatItemStack(GasStack ingredient) {
        ItemStack gas = new ItemStack(MekanismBlocks.GasTank);
        if (!gas.hasTagCompound()){
            gas.setTagCompound(new NBTTagCompound());
        }
        gas.getTagCompound().setInteger("tier", 4);
        GasStack gasStack = new GasStack(ingredient.getGas(), Integer.MAX_VALUE);
        ItemDataUtils.setCompound(gas,"stored", gasStack.write(new NBTTagCompound()));
        return gas;
    }


    @Override
    public GasStack copyIngredient(GasStack ingredient) {
        return ingredient.copy();
    }

    @Override
    public String getErrorInfo(@Nullable GasStack ingredient) {
        ToStringHelper toStringHelper = MoreObjects.toStringHelper(GasStack.class);
        Gas gas = ingredient == null ? null : ingredient.getGas();
        toStringHelper.add("Gas", gas != null ? gas.getLocalizedName() : "null");
        if (ingredient != null) {
            toStringHelper.add("Amount", ingredient.amount);
        }
        return toStringHelper.toString();
    }
}
