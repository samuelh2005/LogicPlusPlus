package me.samuelh2005.logic_plus_plus.content.counter;

import org.jetbrains.annotations.Nullable;

import me.samuelh2005.logic_plus_plus.data.DataBlockEntity;
import me.samuelh2005.logic_plus_plus.data.DataValueInput;
import me.samuelh2005.logic_plus_plus.data.DataValueOutput;
import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CounterBlockEntity extends DataBlockEntity {
    // Sides relative to a NORTH-facing counter (front = NORTH)
    private static final Direction FRONT = Direction.NORTH;  // data output
    private static final Direction BACK = Direction.SOUTH;   // data input
    private static final Direction CLOCK = Direction.EAST;   // redstone only
    private static final Direction MODE = Direction.WEST;    // redstone only

    private final CounterBlockEntityDataValueInput BACK_INPUT = new CounterBlockEntityDataValueInput(this);
    private int value;
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

    // ---- sides ----

    @Nullable
    @Override
    public DataValueOutput getDataOutput(@Nullable Direction side) {
        return side == toAbsolute(FRONT) ? () -> count : null;
    }

    @Nullable
    @Override
    public DataValueInput getDataInput(@Nullable Direction side) {
        return side == toAbsolute(BACK) ? BACK_INPUT : null;
    }

    @Override
    protected void notifyOutputs() {
        super.notifyOutputs();
        // Comparators can read from any side, so keep them in sync
        if (level != null) level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    }

    // ---- events (forwarded from CounterBlock) ----

    public void onNeighborChanged() {
        if (level == null || level.isClientSide) return;

        BlockState state = getBlockState();
        boolean clock = hasRedstoneInput(toAbsolute(CLOCK));
        boolean mode = hasRedstoneInput(toAbsolute(MODE));
        boolean triggered = state.getValue(CounterBlock.TRIGGERED);

        if (clock && !triggered) {
            // Rising edge: act once and remember we're powered
            level.setBlock(worldPosition, state.setValue(CounterBlock.TRIGGERED, true), Block.UPDATE_CLIENTS);
            if (mode) {
                setCount(value);
            } else {
                setCount(count + 1);
            }
        } else if (!clock && triggered) {
            // Falling edge: re-arm for the next pulse
            level.setBlock(worldPosition, state.setValue(CounterBlock.TRIGGERED, false), Block.UPDATE_CLIENTS);
        }
    }

    private void setCount(int newCount) {
        count = newCount;
        setChanged();
        notifyOutputs();
    }

    public static class CounterBlockEntityDataValueInput implements DataValueInput {
        private final CounterBlockEntity blockEntity;

        public CounterBlockEntityDataValueInput(CounterBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public void onInputChanged(int newValue) {
            blockEntity.value = newValue;
        }
    }
}