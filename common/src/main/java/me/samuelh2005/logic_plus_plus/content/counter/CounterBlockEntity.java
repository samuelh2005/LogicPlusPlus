package me.samuelh2005.logic_plus_plus.content.counter;

import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CounterBlockEntity extends BlockEntity {
    private int count;

    public CounterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COUNTER.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("count", count);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        count = tag.getInt("count");
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        setChanged();
    }

    public void onSignal(boolean signal) {
        count++;
        setChanged();
    }
}