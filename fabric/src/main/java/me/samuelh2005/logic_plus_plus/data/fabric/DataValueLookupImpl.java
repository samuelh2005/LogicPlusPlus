package me.samuelh2005.logic_plus_plus.data.fabric;

import org.jetbrains.annotations.Nullable;

import me.samuelh2005.logic_plus_plus.data.DataValueInput;
import me.samuelh2005.logic_plus_plus.data.DataValueLookup;
import me.samuelh2005.logic_plus_plus.data.DataValueOutput;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class DataValueLookupImpl {
    public static final BlockApiLookup<DataValueOutput, @Nullable Direction> OUTPUT =
        BlockApiLookup.get(DataValueLookup.outputId(), DataValueOutput.class, Direction.class);

    public static final BlockApiLookup<DataValueInput, @Nullable Direction> INPUT =
        BlockApiLookup.get(DataValueLookup.inputId(), DataValueInput.class, Direction.class);

    @Nullable
    public static DataValueOutput findOutput(Level level, BlockPos pos, @Nullable Direction side) {
        return OUTPUT.find(level, pos, side);
    }

    @Nullable
    public static DataValueInput findInput(Level level, BlockPos pos, @Nullable Direction side) {
        return INPUT.find(level, pos, side);
    }
}