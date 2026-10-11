package me.samuelh2005.logic_plus_plus.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.block.entity.BlockEntityType;

import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import me.samuelh2005.logic_plus_plus.data.DataBlockEntity;
import me.samuelh2005.logic_plus_plus.data.fabric.DataValueLookupImpl;
import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;

public final class LogicPlusPlusFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Run our common setup.
        LogicPlusPlus.init();

        registerDataBlock(ModBlockEntities.COUNTER.get());
        registerDataBlock(ModBlockEntities.DATA_BUS.get());
        registerDataBlock(ModBlockEntities.VALUE_SOURCE.get());
    }

    // Every data block entity just forwards the lookups to what it declares per side
    private static <T extends DataBlockEntity> void registerDataBlock(BlockEntityType<T> type) {
        DataValueLookupImpl.OUTPUT.registerForBlockEntity((be, side) -> be.getDataOutput(side), type);
        DataValueLookupImpl.INPUT.registerForBlockEntity((be, side) -> be.getDataInput(side), type);
    }
}