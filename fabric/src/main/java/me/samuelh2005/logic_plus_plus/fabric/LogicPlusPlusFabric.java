package me.samuelh2005.logic_plus_plus.fabric;

import net.fabricmc.api.ModInitializer;

import me.samuelh2005.logic_plus_plus.LogicPlusPlus;

public final class LogicPlusPlusFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        LogicPlusPlus.init();
    }
}
