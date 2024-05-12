package mekanism.common.config;

import mekanism.common.config.options.BooleanOption;
import mekanism.common.config.options.DoubleOption;
import mekanism.common.config.options.IntOption;
import mekanism.multiblockmachine.common.block.states.BlockStateMultiblockMachineGenerator.MultiblockMachineGeneratorType;

public class MultiblockMachineConfig extends BaseConfig {

    public final BooleanOption destroyDisabledBlocks = new BooleanOption(this, "multiblock", "DestroyDisabledMultiBlockBlocks", true,
            "If machine is disabled in config, do we set its block to air if it is found in world?");

    public final DoubleOption largewindGeneratorStorage = new DoubleOption(this, "multiblock",
            "MultiblockWindGeneratorStorage", 58800000D, "Energy capable of being stored");

    public final DoubleOption largewindGenerationMax = new DoubleOption(this, "multiblock", "LargeWindGeneratorMax", 294000D,
            "Maximum base generation value of the Large Wind Generator.");

    public final DoubleOption largewindGenerationMin = new DoubleOption(this, "multiblock", "LargeWindGeneratorMin", 60D,
            "Minimum base generation value of the Large Wind Generator.");

    public final IntOption largewindGenerationMinY = new IntOption(this, "multiblock", "LargeWindGenerationMinY", 50,
            "The minimum Y value that affects the Large Wind Generators Power generation.", 50, 255);

    public final IntOption largewindGenerationMaxY = new IntOption(this, "multiblock", "LargeWindGenerationMaxY", 255,
            "The maximum Y value that affects the Large Wind Generators Power generation.", 50, 255);

    public final IntOption largewindGenerationBlastRadius = new IntOption(this, "multiblock", "largewindGeneratorBlastRadius", 45,
            "The range of a large wind turbine when it explodes.");
    public final IntOption largewindGenerationExplodeCount = new IntOption(this, "multiblock", "largewindGenerationExplodeCount", 100, "An explosion can occur after the entity has been in the blade for a number of ticks.");

    public final BooleanOption largewindGenerationExplode = new BooleanOption(this, "multiblock", "largewindGenerationExplode", false, "An explosion occurs when an entity is inside the rotating blades of a large wind turbine.");

    public final BooleanOption largewindGenerationDamage = new BooleanOption(this,"multiblock","largewindGenerationDamage",false,"Whether or not the organism causes harm when it is inside the leaf while it is working");

    public TypeConfigManager<MultiblockMachineGeneratorType> multiblockmachinegeneratorsManager = new TypeConfigManager<>(this, "multiblockmachinegenerators", MultiblockMachineGeneratorType.class,
            MultiblockMachineGeneratorType::getGeneratorsForConfig, MultiblockMachineGeneratorType::getBlockName);
}
