package mekanism.common.content.boiler;

import mekanism.common.tile.multiblock.TileEntityBoilerCasing;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class BoilerSteamTank extends BoilerTank {

    public BoilerSteamTank(TileEntityBoilerCasing tileEntity) {
        super(tileEntity);
    }

    @Override
    @Nullable
    public FluidStack getFluid() {
        return multiblock.structure != null ? multiblock.structure.steamStored : null;
    }

    @Override
    public void setFluid(FluidStack stack) {
        if (multiblock.structure != null) {
            multiblock.structure.steamStored = stack;
        }
    }

    @Override
    public int getCapacity() {
        return multiblock.structure != null ? multiblock.structure.steamVolume * BoilerUpdateProtocol.STEAM_PER_TANK : 0;
    }
}
