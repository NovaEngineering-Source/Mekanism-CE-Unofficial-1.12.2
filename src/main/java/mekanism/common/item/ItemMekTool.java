package mekanism.common.item;

import com.google.common.collect.Multimap;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.OreDictCache;
import mekanism.common.base.IItemNetwork;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.interfaces.IItemHUDProvider;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.WorldEvents;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ItemMekTool extends ItemEnergized implements IItemNetwork, IItemHUDProvider {

    public ItemMekTool() {
        super(MekanismConfig.current().general.toolBatteryCapacity.val());
        setMaxStackSize(1);
    }


    @Override
    public boolean canHarvestBlock(@Nonnull IBlockState state, ItemStack stack) {
        return state.getBlock() != Blocks.BEDROCK;
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack itemstack) {
        return EnumColor.PURPLE + super.getItemStackDisplayName(itemstack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
        super.addInformation(itemstack, world, list, flag);
        MekToolMode mode = getMode(itemstack);
        list.add(LangUtils.localize("tooltip.mode") + ": " + EnumColor.INDIGO + mode.getModeName());
        list.add(LangUtils.localize("tooltip.efficiency") + ": " + EnumColor.INDIGO + mode.getEfficiency());
        list.addAll(MekanismUtils.splitTooltip(LangUtils.localize("tooltip.MekTool1"), itemstack));
        list.addAll(MekanismUtils.splitTooltip(LangUtils.localize("tooltip.MekTool2"), itemstack));
        list.addAll(MekanismUtils.splitTooltip(LangUtils.localize("tooltip.MekTool3"), itemstack));
        list.addAll(MekanismUtils.splitTooltip(EnumColor.RED + LangUtils.localize("tooltip.MekTool4"), itemstack));
    }

    @Override
    public boolean hitEntity(ItemStack itemstack, EntityLivingBase target, EntityLivingBase attacker) {
        double energy = getEnergy(itemstack);
        int energyCost = MekanismConfig.current().general.toolEnergyUsageWeapon.val();
        int minDamage = MekanismConfig.current().general.toolDamageMin.val();
        int damageDifference = MekanismConfig.current().general.toolDamageMax.val() - minDamage;


        //If we don't have enough power use it at a reduced power level
        double percent = 1;
        if (energy < energyCost && energyCost != 0) {
            percent = energy / energyCost;
        }
        float damage = (float) (minDamage + damageDifference * percent);
        if (attacker instanceof EntityPlayer) {
            if (energy > MekanismConfig.current().general.toolBatteryCapacity.val() * 0.9) {
                if (target.getHealth() / target.getMaxHealth() > 0.1) {
                    target.setHealth(0.1F);
                } else {
                    target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), damage * 8096);
                }
            } else if (energy > MekanismConfig.current().general.toolBatteryCapacity.val() * 0.75) {
                target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), damage * 4096);
            } else if (energy > MekanismConfig.current().general.toolBatteryCapacity.val() * 0.5) {
                target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), damage * 2048);
            } else if (energy > MekanismConfig.current().general.toolBatteryCapacity.val() * 0.25) {
                target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), damage * 1024);
            } else {
                target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), damage);
            }
        } else {
            if (energy > MekanismConfig.current().general.toolBatteryCapacity.val() * 0.75) {
                target.attackEntityFrom(DamageSource.causeMobDamage(attacker), damage * 4096);
            } else if (energy > MekanismConfig.current().general.toolBatteryCapacity.val() * 0.5) {
                target.attackEntityFrom(DamageSource.causeMobDamage(attacker), damage * 2048);
            } else if (energy > MekanismConfig.current().general.toolBatteryCapacity.val() * 0.25) {
                target.attackEntityFrom(DamageSource.causeMobDamage(attacker), damage * 1024);
            } else {
                target.attackEntityFrom(DamageSource.causeMobDamage(attacker), damage);
            }
        }
        if (energy > 0) {
            setEnergy(itemstack, energy - energyCost);
        }
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack itemstack, IBlockState state) {
        return getEnergy(itemstack) != 0 ? getMode(itemstack).getEfficiency() : 1F;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemstack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityliving) {
        setEnergy(itemstack, getEnergy(itemstack) - getDestroyEnergy(itemstack, state.getBlockHardness(world, pos)));
        return true;
    }

    private RayTraceResult doRayTrace(IBlockState state, BlockPos pos, EntityPlayer player) {
        Vec3d positionEyes = player.getPositionEyes(1.0F);
        Vec3d playerLook = player.getLook(1.0F);
        double blockReachDistance = player.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d maxReach = positionEyes.add(playerLook.x * blockReachDistance, playerLook.y * blockReachDistance, playerLook.z * blockReachDistance);
        RayTraceResult res = state.collisionRayTrace(player.world, pos, playerLook, maxReach);
        //noinspection ConstantConditions - idea thinks it's nonnull due to package level annotations, but it's not
        return res != null ? res : new RayTraceResult(RayTraceResult.Type.MISS, Vec3d.ZERO, EnumFacing.UP, pos);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        super.onBlockStartBreak(itemstack, pos, player);
        if (!player.world.isRemote && !player.capabilities.isCreativeMode) {
            MekToolMode mode = getMode(itemstack);
            boolean extended = mode == MekToolMode.EXTENDED_VEIN;
            if (extended || mode == MekToolMode.VEIN) {
                IBlockState state = player.world.getBlockState(pos);
                Block block = state.getBlock();
                if (block == Blocks.LIT_REDSTONE_ORE) {
                    block = Blocks.REDSTONE_ORE;
                }
                RayTraceResult raytrace = doRayTrace(state, pos, player);
                ItemStack stack = block.getPickBlock(state, raytrace, player.world, pos, player);
                List<String> names = OreDictCache.getOreDictName(stack);
                boolean isOre = false;
                for (String s : names) {
                    if (s.startsWith("ore") || s.equals("logWood")) {
                        isOre = true;
                        break;
                    }
                }
                if (isOre || extended) {
                    Coord4D orig = new Coord4D(pos, player.world);
                    Set<Coord4D> found = new Finder(player, stack, orig, raytrace, extended ? MekanismConfig.current().general.toolMiningRange.val() : -1).calc();
                    for (Coord4D coord : found) {
                        if (coord.equals(orig)) {
                            continue;
                        }
                        int destroyEnergy = getDestroyEnergy(itemstack, coord.getBlockState(player.world).getBlockHardness(player.world, coord.getPos()));
                        if (getEnergy(itemstack) < destroyEnergy) {
                            continue;
                        }
                        Block block2 = coord.getBlock(player.world);
                        block2.onBlockHarvested(player.world, coord.getPos(), state, player);
                        player.world.playEvent(WorldEvents.BREAK_BLOCK_EFFECTS, coord.getPos(), Block.getStateId(state));
                        block2.dropBlockAsItem(player.world, coord.getPos(), state, 0);
                        player.world.setBlockToAir(coord.getPos());
                        setEnergy(itemstack, getEnergy(itemstack) - destroyEnergy);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entityplayer, @Nonnull EnumHand hand) {
        ItemStack itemstack = entityplayer.getHeldItem(hand);
        if (entityplayer.isSneaking()) {
            if (!world.isRemote) {
                toggleMode(itemstack);
                MekToolMode mode = getMode(itemstack);
                entityplayer.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.GREY + LangUtils.localize("tooltip.modeToggle")
                        + " " + EnumColor.INDIGO + mode.getModeName() + EnumColor.AQUA + " (" + mode.getEfficiency() + ")"));
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
        }
        return new ActionResult<>(EnumActionResult.PASS, itemstack);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            ItemStack stack = player.getHeldItem(hand);
            int diameter = getMode(stack).getDiameter();
            if (diameter > 0) {
                Block block = world.getBlockState(pos).getBlock();
                if (block == Blocks.DIRT || block == Blocks.GRASS_PATH) {
                    return useItemAs(player, world, pos, side, stack, diameter, this::useHoe);
                } else if (block == Blocks.GRASS) {
                    return useItemAs(player, world, pos, side, stack, diameter, this::useShovel);
                }
            }
        }
        return EnumActionResult.PASS;
    }

    private EnumActionResult useItemAs(EntityPlayer player, World world, BlockPos pos, EnumFacing side, ItemStack stack, int diameter, ItemUseConsumer consumer) {
        double energy = getEnergy(stack);
        int hoeUsage = MekanismConfig.current().general.toolEnergyUsageHoe.val();
        if (energy < hoeUsage || consumer.use(stack, player, world, pos, side) == EnumActionResult.FAIL) {
            //Fail if we don't have enough energy or using the item failed
            return EnumActionResult.FAIL;
        }
        double energyUsed = hoeUsage;
        int radius = (diameter - 1) / 2;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (energyUsed + hoeUsage > energy) {
                    break;
                } else if ((x != 0 || z != 0) && consumer.use(stack, player, world, pos.add(x, 0, z), side) == EnumActionResult.SUCCESS) {
                    //Don't attempt to use it on the source location as it was already done above
                    // If we successfully used it in a spot increment how much energy we used
                    energyUsed += hoeUsage;
                }
            }
        }
        setEnergy(stack, energy - energyUsed);
        return EnumActionResult.SUCCESS;
    }

    private EnumActionResult useHoe(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing) {
        if (!player.canPlayerEdit(pos.offset(facing), facing, stack)) {
            return EnumActionResult.FAIL;
        }
        int hook = ForgeEventFactory.onHoeUse(stack, player, world, pos);
        if (hook != 0) {
            return hook > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
        }
        if (facing != EnumFacing.DOWN && world.isAirBlock(pos.up())) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            IBlockState newState = null;
            if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
                newState = Blocks.FARMLAND.getDefaultState();
            } else if (block == Blocks.DIRT) {
                DirtType type = state.getValue(BlockDirt.VARIANT);
                if (type == DirtType.DIRT) {
                    newState = Blocks.FARMLAND.getDefaultState();
                } else if (type == DirtType.COARSE_DIRT) {
                    newState = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.DIRT);
                }
            }
            if (newState != null) {
                setBlock(stack, player, world, pos, newState);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    private EnumActionResult useShovel(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing) {
        if (!player.canPlayerEdit(pos.offset(facing), facing, stack)) {
            return EnumActionResult.FAIL;
        } else if (facing != EnumFacing.DOWN && world.isAirBlock(pos.up())) {
            Block block = world.getBlockState(pos).getBlock();
            if (block == Blocks.GRASS) {
                setBlock(stack, player, world, pos, Blocks.GRASS_PATH.getDefaultState());
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    private void setBlock(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, IBlockState state) {
        worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, state, 11);
            stack.damageItem(1, player);
        }
    }

    private int getDestroyEnergy(ItemStack itemStack, float hardness) {
        int destroyEnergy = MekanismConfig.current().general.toolEnergyUsage.val() * getMode(itemStack).getEfficiency();
        return hardness == 0 ? destroyEnergy / 2 : destroyEnergy;
    }

    public MekToolMode getMode(ItemStack itemStack) {
        return MekToolMode.getFromInt(ItemDataUtils.getInt(itemStack, "mode"));
    }

    public void toggleMode(ItemStack itemStack) {
        ItemDataUtils.setInt(itemStack, "mode", MekToolMode.getNextEnabledAsInt(getMode(itemStack)));
    }

    public void setMode(ItemStack itemStack, MekToolMode mode) {
        ItemDataUtils.setInt(itemStack, "mode", mode.ordinal());
    }

    @Override
    public boolean canSend(ItemStack itemStack) {
        return false;
    }

    @Nonnull
    @Override
    @Deprecated
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multiMap = super.getItemAttributeModifiers(equipmentSlot);
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multiMap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4000000953674316D, 0));
        }
        return multiMap;
    }

    @Override
    public void handlePacketData(ItemStack stack, ByteBuf dataStream) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            int state = dataStream.readInt();
            setMode(stack, MekToolMode.values()[state]);
        }
    }

    @Override
    public void addHUDStrings(List<String> list, EntityPlayer player, ItemStack stack, EntityEquipmentSlot slotType) {
        list.add(LangUtils.localize("tooltip.mode") + ": " + EnumColor.INDIGO + getMode(stack).getModeName());
        list.add(LangUtils.localize("tooltip.efficiency") + ": " + EnumColor.INDIGO + getMode(stack).getEfficiency());
    }

    public enum MekToolMode {
        NORMAL("normal", 20, 3, () -> true),
        SLOW("slow", 8, 1, () -> MekanismConfig.current().general.disassemblerSlowMode.val()),
        FAST("fast", 128, 5, () -> MekanismConfig.current().general.disassemblerFastMode.val()),
        VERY_FAST("very_fast", 256, 5, () -> MekanismConfig.current().general.toolVeryFastMode.val()),
        VEIN("vein", 20, 3, () -> MekanismConfig.current().general.disassemblerVeinMining.val()),
        EXTENDED_VEIN("extended_vein", 20, 3, () -> MekanismConfig.current().general.disassemblerExtendedMining.val()),
        OFF("off", 0, 0, () -> true);

        private final Supplier<Boolean> checkEnabled;
        private final String mode;
        private final int efficiency;
        //Must be odd, or zero
        private final int diameter;

        MekToolMode(String mode, int efficiency, int diameter, Supplier<Boolean> checkEnabled) {
            this.mode = mode;
            this.efficiency = efficiency;
            this.diameter = diameter;
            this.checkEnabled = checkEnabled;
        }

        /**
         * Gets a Mode from its ordinal. NOTE: if this mode is not enabled then it will reset to NORMAL
         */
        public static MekToolMode getFromInt(int ordinal) {
            MekToolMode[] values = values();
            //If it is out of bounds just shift it as if it had gone around that many times
            MekToolMode mode = values[ordinal % values.length];
            return mode.isEnabled() ? mode : NORMAL;
        }

        public static int getNextEnabledAsInt(MekToolMode mode) {
            //Get the next mode
            MekToolMode next = mode.getNext();
            //keep going until we find one that is enabled (we know at the very least NORMAL and OFF are enabled
            while (!next.isEnabled()) {
                next = next.getNext();
            }
            return next.ordinal();
        }

        private MekToolMode getNext() {
            MekToolMode[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public String getModeName() {
            return LangUtils.localize("mekanism.tooltip.mektool." + mode);
        }

        public int getEfficiency() {
            return efficiency;
        }

        public int getDiameter() {
            return diameter;
        }

        public boolean isEnabled() {
            return checkEnabled.get();
        }
    }

    @FunctionalInterface
    interface ItemUseConsumer {

        //Used to reference useHoe and useShovel via lambda references
        EnumActionResult use(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing);
    }

    public static class Finder {

        public static Map<Block, List<Block>> ignoreBlocks = new Object2ObjectOpenHashMap<>();

        static {
            ignoreBlocks.put(Blocks.REDSTONE_ORE, Arrays.asList(Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE));
            ignoreBlocks.put(Blocks.LIT_REDSTONE_ORE, Arrays.asList(Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE));
        }

        public final World world;
        public final ItemStack stack;
        public final Coord4D location;
        public final Set<Coord4D> found = new ObjectOpenHashSet<>();
        private final EntityPlayer player;
        private final RayTraceResult rayTraceResult;
        private final Block startBlock;
        private final boolean isWood;
        private final int maxRange;
        private final int maxCount;

        public Finder(EntityPlayer p, ItemStack s, Coord4D loc, RayTraceResult traceResult, int range) {
            player = p;
            world = p.world;
            stack = s;
            location = loc;
            startBlock = loc.getBlock(world);
            rayTraceResult = traceResult;
            isWood = OreDictCache.getOreDictName(stack).contains("logWood");
            maxRange = range;
            maxCount = MekanismConfig.current().general.toolMiningCount.val() - 1;
        }

        public void loop(Coord4D pointer) {
            if (found.contains(pointer) || found.size() > maxCount) {
                return;
            }
            found.add(pointer);
            for (EnumFacing side : EnumFacing.VALUES) {
                Coord4D coord = pointer.offset(side);
                if (maxRange > 0 && location.distanceTo(coord) > maxRange) {
                    continue;
                }
                if (coord.exists(world)) {
                    Block block = coord.getBlock(world);
                    if (checkID(block)) {
                        ItemStack blockStack = block.getPickBlock(coord.getBlockState(world), rayTraceResult, world, coord.getPos(), player);
                        if (ItemHandlerHelper.canItemStacksStack(stack, blockStack) || (block == startBlock && isWood && coord.getBlockMeta(world) % 4 == stack.getItemDamage() % 4)) {
                            loop(coord);
                        }
                    }
                }
            }
        }

        public Set<Coord4D> calc() {
            loop(location);
            return found;
        }

        public boolean checkID(Block b) {
            Block origBlock = location.getBlock(world);
            List<Block> ignored = ignoreBlocks.get(origBlock);
            return ignored == null ? b == origBlock : ignored.contains(b);
        }
    }


}