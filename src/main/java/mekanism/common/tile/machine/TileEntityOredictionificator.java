package mekanism.common.tile.machine;

import io.netty.buffer.ByteBuf;
import mekanism.api.IConfigCardAccess.ISpecialConfigData;
import mekanism.api.TileNetworkList;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.*;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ISustainedData;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.filter.IFilter;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import mekanism.common.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class TileEntityOredictionificator extends TileEntityContainerBlock implements IRedstoneControl, ISpecialConfigData, ISustainedData, ISecurityTile, ISideConfiguration {


    public static List<String> possibleFilters = Arrays.asList("ingot", "ore", "dust", "nugget");
    public HashList<OredictionificatorFilter> filters = new HashList<>();
    public RedstoneControl controlType = RedstoneControl.DISABLED;

    public TileComponentEjector ejectorComponent;
    public TileComponentConfig configComponent;

    public boolean didProcess;

    public TileComponentSecurity securityComponent = new TileComponentSecurity(this);

    public TileEntityOredictionificator() {
        super(MachineType.OREDICTIONIFICATOR.getBlockName());
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM);
        configComponent.addOutput(TransmissionType.ITEM, new SideData(DataType.NONE, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM, new SideData(DataType.INPUT, new int[]{0}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData(DataType.OUTPUT, new int[]{1}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData(new int[]{0, 1}, new boolean[]{false, true}));
        configComponent.setConfig(TransmissionType.ITEM, new byte[]{1, 1, 1, 1, 1, 2});
        inventory = NonNullListSynchronized.withSize(2, ItemStack.EMPTY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.ITEM, configComponent.getOutputs(TransmissionType.ITEM).get(2));
        ejectorComponent.setInputOutputData(TransmissionType.ITEM, configComponent.getOutputs(TransmissionType.ITEM).get(3));
        doAutoSync = false;
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (playersUsing.size() > 0) {
                for (EntityPlayer player : playersUsing) {
                    Mekanism.packetHandler.sendTo(new TileEntityMessage(this, getGenericPacket(new TileNetworkList())), (EntityPlayerMP) player);
                }
            }
            didProcess = false;
            ItemStack inputStack = inventory.get(0);
            if (MekanismUtils.canFunction(this) && !inputStack.isEmpty() && getValidName(inputStack) != null) {
                ItemStack result = getResult(inputStack);
                if (!result.isEmpty()) {
                    ItemStack outputStack = inventory.get(1);
                    if (outputStack.isEmpty()) {
                        inputStack.shrink(1);
                        if (inputStack.getCount() <= 0) {
                            inventory.set(0, ItemStack.EMPTY);
                        }
                        inventory.set(1, result);
                        didProcess = true;
                    } else if (ItemHandlerHelper.canItemStacksStack(outputStack, result) && outputStack.getCount() < outputStack.getMaxStackSize()) {
                        inputStack.shrink(1);
                        if (inputStack.getCount() <= 0) {
                            inventory.set(0, ItemStack.EMPTY);
                        }
                        outputStack.grow(1);
                        didProcess = true;
                    }
                    markForUpdateSync();
                }
            }
        }
    }

    public String getValidName(ItemStack stack) {
        List<String> def = OreDictCache.getOreDictName(stack);
        for (String s : def) {
            for (String pre : possibleFilters) {
                if (s.startsWith(pre)) {
                    return s;
                }
            }
        }
        return null;
    }

    public ItemStack getResult(ItemStack stack) {
        String s = getValidName(stack);
        if (s == null) {
            return ItemStack.EMPTY;
        }
        List<ItemStack> ores = OreDictionary.getOres(s, false);
        for (OredictionificatorFilter filter : filters) {
            if (filter.filter.equals(s)) {
                if (ores.size() - 1 >= filter.index) {
                    return StackUtils.size(ores.get(filter.index), 1);
                }
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return configComponent.getOutput(TransmissionType.ITEM, side, facing).availableSlots;
    }

    @Override
    public boolean canExtractItem(int slotID, @Nonnull ItemStack itemstack, @Nonnull EnumFacing side) {
        return slotID == 1;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, @Nonnull ItemStack itemstack) {
        if (slotID == 1){
            return false;
        }else if (slotID == 0) {
            return !getResult(itemstack).isEmpty();
        }
        return false;
    }


    @Override
    public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        nbtTags.setInteger("controlType", controlType.ordinal());
        NBTTagList filterTags = new NBTTagList();
        for (OredictionificatorFilter filter : filters) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            filter.write(tagCompound);
            filterTags.appendTag(tagCompound);
        }
        if (filterTags.tagCount() != 0) {
            nbtTags.setTag("filters", filterTags);
        }

    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        controlType = RedstoneControl.values()[nbtTags.getInteger("controlType")];
        if (nbtTags.hasKey("filters")) {
            NBTTagList tagList = nbtTags.getTagList("filters", NBT.TAG_COMPOUND);
            for (int i = 0; i < tagList.tagCount(); i++) {
                filters.add(OredictionificatorFilter.readFromNBT(tagList.getCompoundTagAt(i)));
            }
        }

        //to fix any badly placed blocks in the world
        if (facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            int type = dataStream.readInt();
            if (type == 0) {
                controlType = RedstoneControl.values()[dataStream.readInt()];
                didProcess = dataStream.readBoolean();
                filters.clear();

                int amount = dataStream.readInt();
                for (int i = 0; i < amount; i++) {
                    filters.add(OredictionificatorFilter.readFromPacket(dataStream));
                }
            } else if (type == 1) {
                controlType = RedstoneControl.values()[dataStream.readInt()];
                didProcess = dataStream.readBoolean();
            } else if (type == 2) {
                filters.clear();
                int amount = dataStream.readInt();
                for (int i = 0; i < amount; i++) {
                    filters.add(OredictionificatorFilter.readFromPacket(dataStream));
                }
            }
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(0);
        data.add(controlType.ordinal());
        data.add(didProcess);
        data.add(filters.size());
        for (OredictionificatorFilter filter : filters) {
            filter.write(data);
        }
        return data;
    }

    public TileNetworkList getGenericPacket(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(1);
        data.add(controlType.ordinal());
        data.add(didProcess);
        return data;
    }

    public TileNetworkList getFilterPacket(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(2);
        data.add(filters.size());
        for (OredictionificatorFilter filter : filters) {
            filter.write(data);
        }
        return data;
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {
        if (!world.isRemote) {
            Mekanism.packetHandler.sendUpdatePacket(this);
        }
    }

    @Override
    public NBTTagCompound getConfigurationData(NBTTagCompound nbtTags) {
        NBTTagList filterTags = new NBTTagList();
        for (OredictionificatorFilter filter : filters) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            filter.write(tagCompound);
            filterTags.appendTag(tagCompound);
        }
        if (filterTags.tagCount() != 0) {
            nbtTags.setTag("filters", filterTags);
        }
        return nbtTags;
    }

    @Override
    public void setConfigurationData(NBTTagCompound nbtTags) {
        if (nbtTags.hasKey("filters")) {
            NBTTagList tagList = nbtTags.getTagList("filters", NBT.TAG_COMPOUND);
            for (int i = 0; i < tagList.tagCount(); i++) {
                filters.add(OredictionificatorFilter.readFromNBT(tagList.getCompoundTagAt(i)));
            }
        }
    }

    @Override
    public String getDataType() {
        return getBlockType().getTranslationKey() + "." + fullName + ".name";
    }

    @Override
    public void writeSustainedData(ItemStack itemStack) {
        ItemDataUtils.setBoolean(itemStack, "hasOredictionificatorConfig", true);
        NBTTagList filterTags = new NBTTagList();
        for (OredictionificatorFilter filter : filters) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            filter.write(tagCompound);
            filterTags.appendTag(tagCompound);
        }
        if (filterTags.tagCount() != 0) {
            ItemDataUtils.setList(itemStack, "filters", filterTags);
        }
    }

    @Override
    public void readSustainedData(ItemStack itemStack) {
        if (ItemDataUtils.hasData(itemStack, "hasOredictionificatorConfig")) {
            if (ItemDataUtils.hasData(itemStack, "filters")) {
                NBTTagList tagList = ItemDataUtils.getList(itemStack, "filters");
                for (int i = 0; i < tagList.tagCount(); i++) {
                    filters.add(OredictionificatorFilter.readFromNBT(tagList.getCompoundTagAt(i)));
                }
            }
        }
    }

    @Override
    public RedstoneControl getControlType() {
        return controlType;
    }

    @Override
    public void setControlType(RedstoneControl type) {
        controlType = type;
    }

    @Override
    public boolean canPulse() {
        return true;
    }

    @Override
    public TileComponentSecurity getSecurity() {
        return securityComponent;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing side) {
        if (isCapabilityDisabled(capability, side)) {
            return false;
        }
        return capability == Capabilities.CONFIG_CARD_CAPABILITY || capability == Capabilities.SPECIAL_CONFIG_DATA_CAPABILITY || super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing side) {
        if (isCapabilityDisabled(capability, side)) {
            return null;
        } else if (capability == Capabilities.CONFIG_CARD_CAPABILITY || capability == Capabilities.SPECIAL_CONFIG_DATA_CAPABILITY) {
            return (T) this;
        }
        return super.getCapability(capability, side);
    }

    @Override
    public boolean isCapabilityDisabled(@Nonnull Capability<?> capability, EnumFacing side) {
        return configComponent.isCapabilityDisabled(capability, side, facing) || super.isCapabilityDisabled(capability, side);
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing facing) {
        return facing != EnumFacing.DOWN && facing != EnumFacing.UP;
    }

    public static class OredictionificatorFilter implements IFilter {

        public String filter;
        public int index;

        public static OredictionificatorFilter readFromNBT(NBTTagCompound nbtTags) {
            OredictionificatorFilter filter = new OredictionificatorFilter();
            filter.read(nbtTags);
            return filter;
        }

        public static OredictionificatorFilter readFromPacket(ByteBuf dataStream) {
            OredictionificatorFilter filter = new OredictionificatorFilter();
            filter.read(dataStream);
            return filter;
        }

        public void write(NBTTagCompound nbtTags) {
            nbtTags.setString("filter", filter);
            nbtTags.setInteger("index", index);
        }

        protected void read(NBTTagCompound nbtTags) {
            filter = nbtTags.getString("filter");
            index = nbtTags.getInteger("index");
        }

        public void write(TileNetworkList data) {
            data.add(filter);
            data.add(index);
        }

        protected void read(ByteBuf dataStream) {
            filter = PacketHandler.readString(dataStream);
            index = dataStream.readInt();
        }

        @Override
        public OredictionificatorFilter clone() {
            OredictionificatorFilter newFilter = new OredictionificatorFilter();
            newFilter.filter = filter;
            newFilter.index = index;
            return newFilter;
        }

        @Override
        public int hashCode() {
            int code = 1;
            code = 31 * code + filter.hashCode();
            return code;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof OredictionificatorFilter ore && ore.filter.equals(filter);
        }
    }

    @Override
    public TileComponentConfig getConfig() {
        return configComponent;
    }

    @Override
    public EnumFacing getOrientation() {
        return facing;
    }

    @Override
    public TileComponentEjector getEjector() {
        return ejectorComponent;
    }
}
