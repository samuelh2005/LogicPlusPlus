package me.samuelh2005.logic_plus_plus.content.value_source;

import org.jetbrains.annotations.Nullable;

import me.samuelh2005.logic_plus_plus.data.DataBlockEntity;
import me.samuelh2005.logic_plus_plus.data.DataValueInput;
import me.samuelh2005.logic_plus_plus.data.DataValueOutput;
import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ValueSourceBlockEntity extends DataBlockEntity {
    private int value;

    public ValueSourceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VALUE_SOURCE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("value", value);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        value = tag.getInt("value");
    }

    public int getValue() {
        return value;
    }

    // ---- sides ----

    @Nullable
    @Override
    public DataValueOutput getDataOutput(@Nullable Direction side) {
        return () -> value;
    }

    @Nullable
    @Override
    public DataValueInput getDataInput(@Nullable Direction side) {
        return null;
    }

    @Override
    protected void notifyOutputs() {
        super.notifyOutputs();
        // Comparators can read from any side, so keep them in sync
        if (level != null) level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    }
}