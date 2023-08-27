package mekanism.common.tile;

import java.util.Map;
import mekanism.api.EnumColor;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.SideData;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.AdvancedMachineInput;
import mekanism.common.recipe.machines.FarmRecipe;
import mekanism.common.tile.prefab.TileEntityFarmMachine;
import mekanism.common.util.InventoryUtils;
import net.minecraft.util.EnumFacing;

public class TileEntityOrganicFarm extends TileEntityFarmMachine<FarmRecipe> {
    public TileEntityOrganicFarm() {
        super("injection", MachineType.ORGANIC_FARM, BASE_TICKS_REQUIRED, BASE_GAS_PER_TICK);
        configComponent.addSupported(TransmissionType.GAS);
        configComponent.addOutput(TransmissionType.GAS, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.GAS, new SideData("Gas", EnumColor.RED, new int[]{0}));
        configComponent.fillConfig(TransmissionType.GAS, 1);
        configComponent.setCanEject(TransmissionType.GAS, false);
    }

    @Override
    public Map<AdvancedMachineInput, FarmRecipe> getRecipes() {
        return Recipe.ORGANIC_FARM.get();
    }

    @Override
    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
        if (canReceiveGas(side, stack.getGas())) {
            return gasTank.receive(stack, doTransfer);
        }
        return 0;
    }

    @Override
    public boolean canReceiveGas(EnumFacing side, Gas type) {
        return configComponent.getOutput(TransmissionType.GAS, side, facing).hasSlot(0) && gasTank.canReceive(type) && isValidGas(type);

    }


    @Override
    public boolean isValidGas(Gas gas) {
        return Recipe.ORGANIC_FARM.containsRecipe(gas);
    }

    @Override
    public boolean upgradeableSecondaryEfficiency() {
        return true;
    }

    @Override
    public boolean useStatisticalMechanics() {
        return true;
    }


}
