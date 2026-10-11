package me.samuelh2005.logic_plus_plus.content.data_bus;

import net.minecraft.util.StringRepresentable;

public enum DataBusConnection implements StringRepresentable {
    NONE("none"),
    CONNECTED("connected"),
    DISABLED("disabled");

    private final String serializedName;

    DataBusConnection(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    public boolean isConnected(DataBusConnection other) {
        // Two connections are considered connected if neither is NONE or DISABLED
        // In other words, they are connected if both are CONNECTED
        return this == CONNECTED && other == CONNECTED;
    }
}