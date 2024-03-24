package mekanism.generators.common.content.turbine;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.api.Coord4D;
import mekanism.common.config.MekanismConfig;
import mekanism.common.multiblock.SynchronizedData;
import mekanism.common.tile.TileEntityGasTank.GasMode;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Map;

public class SynchronizedTurbineData extends SynchronizedData<SynchronizedTurbineData> {

    public static final float ROTATION_THRESHOLD = 0.001F;
    public static Map<String, Float> clientRotationMap = new Object2ObjectOpenHashMap<>();

    @Nullable
    public FluidStack fluidStored;

    @Nullable
    public FluidStack prevFluid;

    public double electricityStored;

    public GasMode dumpMode = GasMode.IDLE;

    public int blades;
    public int vents;
    public int coils;
    public int condensers;

    public int lowerVolume;

    public Coord4D complex;

    public int lastSteamInput;
    public int newSteamInput;

    public int flowRemaining;

    public int clientDispersers;
    public int clientFlow;
    public float clientRotation;

    public int getDispersers() {
        return (volLength - 2) * (volWidth - 2) - 1;
    }

    public int getFluidCapacity() {
        return lowerVolume * TurbineUpdateProtocol.FLUID_PER_TANK;
    }

    public double getEnergyCapacity() {
        return volume * MekanismConfig.current().generators.turbineGeneratorStorage.val(); //16 MJ energy capacity per volume
    }

    public boolean needsRenderUpdate() {
        if ((fluidStored == null && prevFluid != null) || (fluidStored != null && prevFluid == null)) {
            return true;
        }
        if (fluidStored != null) {
            return (fluidStored.getFluid() != prevFluid.getFluid()) || (fluidStored.amount != prevFluid.amount);
        }
        return false;
    }
}
