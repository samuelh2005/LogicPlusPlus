package me.samuelh2005.logic_plus_plus.content.data_bus;

import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;
import me.samuelh2005.logic_plus_plus.systems.ValueSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DataBusBlockEntity extends BlockEntity {
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

    public ValueSource getValueOutput() {
        return new ValueSource(value);
    }
}