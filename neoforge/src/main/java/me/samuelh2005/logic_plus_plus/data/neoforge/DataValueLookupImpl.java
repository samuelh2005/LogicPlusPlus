package me.samuelh2005.logic_plus_plus.data.neoforge;

import org.jetbrains.annotations.Nullable;

import me.samuelh2005.logic_plus_plus.data.DataValueInput;
import me.samuelh2005.logic_plus_plus.data.DataValueLookup;
import me.samuelh2005.logic_plus_plus.data.DataValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class DataValueLookupImpl {
    public static final BlockCapability<DataValueOutput, @Nullable Direction> OUTPUT =
        BlockCapability.createSided(DataValueLookup.outputId(), DataValueOutput.class);

    public static final BlockCapability<DataValueInput, @Nullable Direction> INPUT =
        BlockCapability.createSided(DataValueLookup.inputId(), DataValueInput.class);

    @Nullable
    public static DataValueOutput findOutput(Level level, BlockPos pos, @Nullable Direction side) {
        return level.getCapability(OUTPUT, pos, side);
    }

    @Nullable
    public static DataValueInput findInput(Level level, BlockPos pos, @Nullable Direction side) {
        return level.getCapability(INPUT, pos, side);
    }
}