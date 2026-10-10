package me.samuelh2005.logic_plus_plus.content.digital_cable;

import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;
import me.samuelh2005.logic_plus_plus.systems.ValueSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalCableBlockEntity extends BlockEntity {
    private int value;

    public DigitalCableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIGITAL_CABLE.get(), pos, state);
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