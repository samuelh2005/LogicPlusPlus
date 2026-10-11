package me.samuelh2005.logic_plus_plus.neoforge;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import me.samuelh2005.logic_plus_plus.data.DataBlockEntity;
import me.samuelh2005.logic_plus_plus.data.neoforge.DataValueLookupImpl;
import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;

@Mod(LogicPlusPlus.MOD_ID)
public final class LogicPlusPlusNeoForge {
    public LogicPlusPlusNeoForge(IEventBus modBus) {
        // Run our common setup.
        LogicPlusPlus.init();
        modBus.addListener(LogicPlusPlusNeoForge::registerCaps);
        modBus.addListener(LogicPlusPlusNeoForge::clientSetup);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        LogicPlusPlus.initClient();
    }

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        registerDataBlock(event, ModBlockEntities.COUNTER.get());
        registerDataBlock(event, ModBlockEntities.DATA_BUS.get());
        registerDataBlock(event, ModBlockEntities.DIGITAL_DISPLAY.get());
        registerDataBlock(event, ModBlockEntities.VALUE_SOURCE.get());
    }

    // Every data block entity just forwards the lookups to what it declares per side
    private static <T extends DataBlockEntity> void registerDataBlock(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
        event.registerBlockEntity(DataValueLookupImpl.OUTPUT, type, (be, side) -> be.getDataOutput(side));
        event.registerBlockEntity(DataValueLookupImpl.INPUT, type, (be, side) -> be.getDataInput(side));
    }
}