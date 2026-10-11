package me.samuelh2005.logic_plus_plus.fabric.client;

import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import net.fabricmc.api.ClientModInitializer;

public final class LogicPlusPlusFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        LogicPlusPlus.initClient();
    }
}
