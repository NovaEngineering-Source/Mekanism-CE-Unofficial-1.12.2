package mekanism.common.tile.component.config;

import mekanism.api.EnumColor;

public enum DataType {
    NONE("None", EnumColor.GREY),
    INPUT("Input", EnumColor.RED),
    INPUT_1("Input_1", EnumColor.DARK_RED),
    INPUT_2("Input_2", EnumColor.ORANGE),
    OUTPUT("Output", EnumColor.INDIGO),
    OUTPUT_1("Output_1", EnumColor.DARK_BLUE),
    OUTPUT_2("Output_2", EnumColor.AQUA),
    INPUT_OUTPUT("Input_Output", EnumColor.PURPLE),
    INPUT_EXTRA("Input_Extra", EnumColor.ORANGE),
    ENERGY("Energy", EnumColor.BRIGHT_GREEN),
    EXTRA("Extra", EnumColor.YELLOW),
    GAS("Gas", EnumColor.INDIGO),
    FLUID("Fluid", EnumColor.DARK_AQUA),
    EMPTY("Empty",EnumColor.BLACK);

    private static final DataType[] TYPES = values();
    private final EnumColor color;
    private final String name;

    DataType(String langEntry, EnumColor color) {
        this.color = color;
        this.name = langEntry;
    }
    public EnumColor getColor() {
        return color;
    }
    public String getName() {
        return name;
    }


}
