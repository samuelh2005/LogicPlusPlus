package me.samuelh2005.logic_plus_plus.content.data_bus;

import org.jetbrains.annotations.Nullable;

import me.samuelh2005.logic_plus_plus.data.DataBlockEntity;
import me.samuelh2005.logic_plus_plus.data.DataValueOutput;
import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class DataBusBlockEntity extends DataBlockEntity {
    private int value;

    public DataBusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DATA_BUS.get(), pos, state);
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

    public void setValue(int value) {
        this.value = value;
        setChanged();
    }

    /** A bus only outputs on sides that are actually connected (not NONE, not DISABLED). */
    @Nullable
    @Override
    public DataValueOutput getDataOutput(@Nullable Direction side) {
        if (side == null) return null;
        boolean connected = getBlockState().getValue(DataBusBlock.getPropertyForDirection(side)) == DataBusConnection.CONNECTED;
        return connected ? () -> value : null;
    }
}