package mekanism.common.config;

import mekanism.common.config.options.BooleanOption;
import mekanism.common.config.options.IntOption;


public class MEKCEConfig extends BaseConfig {

    public final BooleanOption EnableDiamondCompat = new BooleanOption(this, "mekce", "EnableDiamondCompat", true,
            "Allows oredict'ed diamonds to be used in the enrichment chamber, like synthetic diamonds.");

    public final BooleanOption EnablePoorOresCompat = new BooleanOption(this, "mekce", "EnablePoorOresCompat", true,
            "Allows poor ores from railcraft to be used in the purification chamber and gives one clump ie one ingot.");

    public final BooleanOption EnableQuartzCompat = new BooleanOption(this, "mekce", "EnableQuartzCompat", true,
            "Allows quartz dust to be enriched into quartz Also allows quartz ore to be enriched into quartz dust");

    public final BooleanOption EnableSiliconCompat = new BooleanOption(this, "mekce", "EnableSiliconCompat", true,
            "When a mod that adds silicon (galacticraft, enderio, projectred and ae2) is detected, recipe for control circuit is changed from using iron to silicon in the metalurgic infuser");

    //public final BooleanOption enableBoPProgression = new BooleanOption(this, "mekce", "enableBoPProgression", true,
    //        "when true and biome's o plenty is installed atomic alloy is made by using ender instead of obsidian");

    //public final BooleanOption EnableSingleUseCardboxes = new BooleanOption(this, "mekce", "EnableSingleUseCardboxes", true,
    //        "This allows to force single use on cardboxes or not");

    public final BooleanOption ShoHiddenGas = new BooleanOption(this, "mekce", "ShowHiddenGases", true, "Displays hidden gas in creative gas tanks, which is invalid if PrefilledGasTanks is not enabled");

    public final BooleanOption EmptyToCreateBin = new BooleanOption(this, "mekce", "EmptytoCreateBin", false, "Let Configurator clear Create Bin");

    public final BooleanOption EmptyToCreateGasTank = new BooleanOption(this, "mekce", "EmptyToCreateGasTank", false, "Let Configurator clear Create Gas Tank");

    public final BooleanOption EmptytoCreateFluidTank = new BooleanOption(this, "mekce", "EmptytoCreateFluidTank", false, "Let Configurator clear Create Fluid Tank");

    public final BooleanOption GasTOP = new BooleanOption(this, "mekce", "GasTop", false, "If true, the shutdown requires The One Probe item to sneak up to display the amount inside the gas tank");

    public final IntOption GasTopBarBorder = new IntOption(this, "mekce", "GasTopBarBorder", 0xfffee140, "Color for the Gas bar border");

    public final BooleanOption RotaryCondensentratorAuto = new BooleanOption(this, "mekce", "RotaryCondensentratorAuto", false, "Turn off automatic change gas and fluid ejection mode in Rotary Condensentrator?");

    public final IntOption ItemEjectionDelay = new IntOption(this, "mekce", "ItemEjectionDelay", 10, "Every how many ticks pop up an item, the default is 10 ticks", 1, Integer.MAX_VALUE);

    public final BooleanOption ItemsEjectWithoutDelay = new BooleanOption(this, "mekce", "ItemsEjectWithoutDelay", false, "If true, the Item Ejection Delay is ignored");

    public final IntOption GasEjectionSpeed = new IntOption(this, "mekce", "GasEjectionSpeed", 256, "The speed at which the machine ejects gas", 1, Integer.MAX_VALUE);

    public final IntOption FluidEjectionSpeed = new IntOption(this, "mekce", "FluidEjectionSpeed", 256, "The speed at which the machine ejects fluid", 1, Integer.MAX_VALUE);

    public final BooleanOption GasEjectionSettings = new BooleanOption(this, "mekce", "GasEjectionSettings", false, "If true, the gas ejection is based on the number of tanks in the tank");

    public final BooleanOption FluidEjectionSettings = new BooleanOption(this, "mekce", "FluidEjectionSettings", false, "If true, the fluid ejection is based on the number of tanks in the tank");

    public final IntOption MAXSpeedUpgrade = new IntOption(this, "mekce", "MAXSpeedUpgrade", 8, "The maximum number of speed upgrades that can be installed", 1, Integer.MAX_VALUE).setRequiresGameRestart(true);

    public final IntOption MAXEnergyUpgrade = new IntOption(this, "mekce", "MAXEnergyUpgrade", 8, "The maximum number of energy upgrades that can be installed", 1, Integer.MAX_VALUE).setRequiresGameRestart(true);

    public final IntOption MAXGasUpgrade = new IntOption(this, "mekce", "MAXGasUpgrade", 8, "The maximum number of gas upgrades that can be installed", 1, Integer.MAX_VALUE).setRequiresGameRestart(true);

    public final IntOption MAXMufflingUpgrade = new IntOption(this, "mekce", "MAXMufflingUpgrade", 4, "The maximum number of muffling upgrades that can be installed", 1, Integer.MAX_VALUE).setRequiresGameRestart(true);

    public final IntOption MAXSpeedUpgradeSize = new IntOption(this, "mekce", "MAXSpeedUpgradeSize", 8, "The maximum number of stacks that can be stacked for speed upgrades", 1, 64).setRequiresGameRestart(true);

    public final IntOption MAXEnergyUpgradeSize = new IntOption(this, "mekce", "MAXEnergyUpgradeSize", 8, "The maximum number of stacks that can be stacked for energy upgrades", 1, 64).setRequiresGameRestart(true);

    public final IntOption MAXGasUpgradeSize = new IntOption(this, "mekce", "MAXGasUpgradeSize", 8, "The maximum number of stacks that can be stacked for gas upgrades", 1, 64).setRequiresGameRestart(true);

    public final IntOption MAXMufflingUpgradeSize = new IntOption(this, "mekce", "MAXMufflingUpgradeSize", 4, "The maximum number of stacks that can be stacked for muffling upgrades", 1, 64).setRequiresGameRestart(true);

    public final IntOption MAXspeedmachines= new IntOption(this, "mekce","Maximum speed multiplier for some machines",256,"Modify the maximum speed multiplier for some machines",1,Integer.MAX_VALUE);

    public final BooleanOption EnableBuff = new BooleanOption(this, "mekce","EnableBuff",false,"If true, a buff effect will be added to the player each time Canteen is used");
}
