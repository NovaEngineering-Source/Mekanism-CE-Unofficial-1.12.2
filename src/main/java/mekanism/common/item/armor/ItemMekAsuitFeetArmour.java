package mekanism.common.item.armor;

import com.google.common.collect.Multimap;
import mekanism.api.EnumColor;
import mekanism.client.model.mekasuitarmour.ModelMekAsuitBoot;
import mekanism.common.MekanismItems;
import mekanism.common.config.MekanismConfig;
import mekanism.common.moduleUpgrade;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.LangUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ItemMekAsuitFeetArmour extends ItemMekaSuitArmor {

    public ItemMekAsuitFeetArmour() {
        super(EntityEquipmentSlot.FEET);
    }

    @Override
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
        return armorType == EntityEquipmentSlot.FEET;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        ModelMekAsuitBoot armorModel = new ModelMekAsuitBoot();
        Render<AbstractClientPlayer> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entityLiving);
        if (render instanceof RenderPlayer) {
            armorModel.setModelAttributes(_default);
        }
        return armorModel;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        UUID uuid = new UUID((getTranslationKey(stack) + slot).hashCode(), 0);
        if (slot == EntityEquipmentSlot.FEET) {
            multimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(uuid, "Terrasteel modifier " + EntityEquipmentSlot.FEET, 1.5D, 0));
        }
        return multimap;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, @NotNull ItemStack armor, DamageSource source, double damage, int slot) {
        ArmorProperties properties = new ArmorProperties(0, 0, 0);
        if (this == MekanismItems.MekAsuitBoots) {
            properties = new ArmorProperties(1, MekanismConfig.current().general.MekaSuitBootsDamageRatio.val(), MekanismConfig.current().general.MekaSuitBootsDamageMax.val());
            properties.Toughness = 3.0F;
        }

        return properties;
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, @NotNull ItemStack armor, int slot) {
        if (armor.getItem() == MekanismItems.MekAsuitBoots) {
            return 3;
        }
        return 0;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, @NotNull ItemStack stack, DamageSource source, int damage, int slot) {
    }

    public FreeRunnerMode getMode(ItemStack itemStack) {
        return FreeRunnerMode.values()[ItemDataUtils.getInt(itemStack, "mode")];
    }

    public void setMode(ItemStack itemStack, FreeRunnerMode mode) {
        ItemDataUtils.setInt(itemStack, "mode", mode.ordinal());
    }

    public void incrementMode(ItemStack itemStack) {
        setMode(itemStack, getMode(itemStack).increment());
    }


    public enum FreeRunnerMode {
        NORMAL("tooltip.freerunner.regular", EnumColor.DARK_GREEN),
        SAFETY("tooltip.freerunner.safety", EnumColor.ORANGE),
        DISABLED("tooltip.freerunner.disabled", EnumColor.DARK_RED);

        private String unlocalized;
        private EnumColor color;

        FreeRunnerMode(String unlocalized, EnumColor color) {
            this.unlocalized = unlocalized;
            this.color = color;
        }

        public FreeRunnerMode increment() {
            return ordinal() < values().length - 1 ? values()[ordinal() + 1] : values()[0];
        }

        public String getName() {
            return color + LangUtils.localize(unlocalized);
        }
    }

    @Override
    public List<moduleUpgrade> getValidModule(ItemStack stack) {
        List<moduleUpgrade> list = super.getValidModule(stack);
        list.add(moduleUpgrade.HYDRAULIC_PROPULSION_UNIT);
        list.add(moduleUpgrade.MAGNETIC_ATTRACTION_UNIT);
        list.add(moduleUpgrade.FROST_WALKER_UNIT);
        return list;
    }


    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
        if (!world.isRemote) {
            ItemStack feetStack = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            if (feetStack.getItem() instanceof ItemMekAsuitFeetArmour armour) {
                if (isUpgradeInstalled(feetStack, moduleUpgrade.FROST_WALKER_UNIT)) {
                    if (armour.getEnergy(feetStack) > 500D) {
                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FROST_WALKER, feetStack) == 0) {
                            feetStack.addEnchantment(Enchantments.FROST_WALKER, armour.getUpgrades(moduleUpgrade.FROST_WALKER_UNIT));
                            hasEffect(feetStack);
                            armour.setEnergy(feetStack, armour.getEnergy(feetStack) - 500D);
                        }
                    } else {
                        removeEnchantment(feetStack);
                    }
                }
            }
        }
    }

    public void removeEnchantment(ItemStack stack) {
        NBTTagList list = stack.getEnchantmentTagList();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            int id = compound.getShort("id");
            Enchantment e = Enchantment.getEnchantmentByID(id);
            if (e == Enchantments.FROST_WALKER) {
                list.removeTag(i);
            }
        }
    }

}
