package mekanism.common.config;

import io.netty.buffer.ByteBuf;
import mekanism.common.config.options.BooleanOption;
import mekanism.common.config.options.DoubleOption;
import mekanism.common.config.options.IntOption;

/**
 * Created by Thiakil on 15/03/2019.
 */
public class ClientConfig extends BaseConfig {

    public final BooleanOption enablePlayerSounds = new BooleanOption(this, "client", "EnablePlayerSounds", true,
            "Play sounds for Jetpack/Gas Mask/Flamethrower (all players).");

    public final BooleanOption enableMachineSounds = new BooleanOption(this, "client", "EnableMachineSounds", true,
            "If enabled machines play their sounds while running.");

    public final BooleanOption holidays = new BooleanOption(this, "client", "Holidays", true,
            "Christmas/New Years greetings in chat.");

    public final DoubleOption baseSoundVolume = new DoubleOption(this, "client", "SoundVolume", 1D,
            "Adjust Mekanism sounds' base volume. < 1 is softer, higher is louder.");

    public final BooleanOption machineEffects = new BooleanOption(this, "client", "MachineEffects", true,
            "Show particles when machines active.");

    public final BooleanOption enableAmbientLighting = new BooleanOption(this, "client", "EnableAmbientLighting", true,
            "Should active machines produce block light.");

    public final IntOption ambientLightingLevel = new IntOption(this, "client", "AmbientLightingLevel", 15,
            "How much light to produce if ambient lighting is enabled.", 1, 15);

    public final BooleanOption opaqueTransmitters = new BooleanOption(this, "client", "OpaqueTransmitterRender", false,
            "If true, don't render Cables/Pipes/Tubes as transparent and don't render their contents.");

    public final BooleanOption allowConfiguratorModeScroll = new BooleanOption(this, "client", "ConfiguratorModeScroll", true,
            "Allow sneak+scroll to change Configurator modes.");

    public final BooleanOption enableMultiblockFormationParticles = new BooleanOption(this, "client", "MultiblockFormParticles", true,
            "Set to false to prevent particle spam when loading multiblocks (notification message will display instead).");

    public final BooleanOption alignHUDLeft = new BooleanOption(this, "client", "AlignHUDLeft", true,
            "Align HUD with left (if true) or right (if false)");

    public final BooleanOption enableHUD = new BooleanOption(this, "client", "enableHUD", true, "Enable item information HUD during gameplay");

    public final BooleanOption allowFlamethrowerModeScroll = new BooleanOption(this, "client", "FlamethrowerModeScroll", true,
            "Allow sneak+scroll to change Flamethrower modes.");

    public final BooleanOption allowAtomicDisassemblerModeScroll = new BooleanOption(this, "client", "AtomicDisassemblerModeScroll", true,
            "Allow sneak+scroll to change Atomic Disassembler modes.");

    public final BooleanOption allowMekToolModeScroll = new BooleanOption(this, "client", "AtomicDisassemblerModeScroll", true,
            "Allow sneak+scroll to change MekTool modes.");

    public final IntOption AllMekGuiBg = new IntOption(this,"client","AllMekGuiBg",0xFFFFFFFF,"All mekanism GUI background colors");

    @Override
    public void write(ByteBuf config) {
        throw new UnsupportedOperationException("Client config shouldn't be synced");
    }

    @Override
    public void read(ByteBuf config) {
        throw new UnsupportedOperationException("Client config shouldn't be synced");
    }
}
