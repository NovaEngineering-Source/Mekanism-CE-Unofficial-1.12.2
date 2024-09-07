package mekanism.common.integration;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.common.tile.*;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.multiblock.TileEntityInductionCell;
import mekanism.common.tile.multiblock.TileEntityInductionProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;

import javax.annotation.Nonnull;
import java.util.List;

@Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = MekanismHooks.WAILA_MOD_ID)
public class WailaDataProvider implements IWailaDataProvider {

    @Method(modid = MekanismHooks.WAILA_MOD_ID)
    public static void register(IWailaRegistrar registrar) {
        WailaDataProvider provider = new WailaDataProvider();

        registrar.registerHeadProvider(provider, TileEntityInductionCell.class);
        registrar.registerHeadProvider(provider, TileEntityInductionProvider.class);
        registrar.registerHeadProvider(provider, TileEntityFactory.class);
        registrar.registerHeadProvider(provider, TileEntityBoundingBlock.class);
        registrar.registerHeadProvider(provider, TileEntityAdvancedBoundingBlock.class);
        registrar.registerHeadProvider(provider, TileEntityFluidTank.class);
        registrar.registerHeadProvider(provider, TileEntityGasTank.class);
        registrar.registerHeadProvider(provider, TileEntityBin.class);
        registrar.registerHeadProvider(provider, TileEntityEnergyCube.class);
    }

    @Nonnull
    @Override
    @Method(modid = MekanismHooks.WAILA_MOD_ID)
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    @Method(modid = MekanismHooks.WAILA_MOD_ID)
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity tile = accessor.getTileEntity();

        if (tile instanceof TileEntityInductionCell cell) {
            currenttip.set(0, EnumColor.WHITE + cell.getName());
        } else if (tile instanceof TileEntityInductionProvider provider) {
            currenttip.set(0, EnumColor.WHITE + provider.getName());
        } else if (tile instanceof TileEntityFactory factory) {
            currenttip.set(0, EnumColor.WHITE + factory.getName());
        } else if (tile instanceof TileEntityFluidTank tank) {
            currenttip.set(0, EnumColor.WHITE + tank.getName());
        } else if (tile instanceof TileEntityGasTank gasTank) {
            currenttip.set(0, EnumColor.WHITE + gasTank.getName());
        } else if (tile instanceof TileEntityBin bin) {
            currenttip.set(0, EnumColor.WHITE + bin.getName());
        } else if (tile instanceof TileEntityEnergyCube cube) {
            currenttip.set(0, EnumColor.WHITE + cube.getName());
        } else if (tile instanceof TileEntityBoundingBlock bound) {
            Coord4D coord = new Coord4D(bound.getPos(), tile.getWorld());
            //TODO: Switch to a smarter way to get the main tile's name - i.e. block name
            if (bound.receivedCoords && coord.getTileEntity(tile.getWorld()) instanceof IInventory inventory) {
                currenttip.set(0, EnumColor.WHITE + inventory.getName());
            }
        }
        return currenttip;
    }

    @Nonnull
    @Override
    @Method(modid = MekanismHooks.WAILA_MOD_ID)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Nonnull
    @Override
    @Method(modid = MekanismHooks.WAILA_MOD_ID)
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Nonnull
    @Override
    @Method(modid = MekanismHooks.WAILA_MOD_ID)
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        return tag;
    }
}
