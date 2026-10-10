package me.samuelh2005.logic_plus_plus.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;
import me.samuelh2005.logic_plus_plus.neoforge.systems.ValueSourceProviderImpl;

@Mod(LogicPlusPlus.MOD_ID)
public final class LogicPlusPlusNeoForge {
    public LogicPlusPlusNeoForge() {
        // Run our common setup.
        LogicPlusPlus.init();
    }

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(ValueSourceProviderImpl.VALUE_SOURCE,
            ModBlockEntities.COUNTER.get(),
            (be, side) -> be.getValueOutput());
    
        event.registerBlockEntity(ValueSourceProviderImpl.VALUE_SOURCE,
            ModBlockEntities.DIGITAL_CABLE.get(),
            (be, side) -> be.getValueOutput());
    }
}
