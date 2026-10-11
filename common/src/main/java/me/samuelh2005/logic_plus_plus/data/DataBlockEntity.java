package me.samuelh2005.logic_plus_plus.data;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Base for every block entity that takes part in the data network.
 * It stores no data. A device declares what each side does by overriding
 * getDataOutput / getDataInput; this class only provides the plumbing.
 */
public abstract class DataBlockEntity extends BlockEntity {
    protected DataBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ---- what this device exposes (absolute sides; null = nothing on that side) ----

    @Nullable
    public DataValueOutput getDataOutput(@Nullable Direction side) {
        return null;
    }

    @Nullable
    public DataValueInput getDataInput(@Nullable Direction side) {
        return null;
    }

    // ---- helpers ----

    /** Tells the neighbours we expose an output to that they should re-read us. */
    protected void notifyOutputs() {
        if (level == null || level.isClientSide) return;
        for (Direction dir : Direction.values()) {
            if (getDataOutput(dir) != null) {
                level.neighborChanged(worldPosition.relative(dir), getBlockState().getBlock(), worldPosition);
            }
        }
    }

    /** The data output of whatever is next to us on the given absolute side, or null. */
    @Nullable
    protected DataValueOutput findOutputAt(Direction side) {
        if (level == null) return null;
        return DataValueLookup.findOutput(level, worldPosition.relative(side), side.getOpposite());
    }

    /** Convenience: the value coming in from that side, 0 if nothing is there. */
    protected int readValueAt(Direction side) {
        DataValueOutput out = findOutputAt(side);
        return out == null ? 0 : out.getValue();
    }

    protected boolean hasRedstoneInput(Direction side) {
        return level != null && level.getControlInputSignal(worldPosition.relative(side), side, false) > 0;
    }

    /** Converts a side relative to a NORTH-facing block (front = NORTH) to an absolute side. */
    protected Direction toAbsolute(Direction relative) {
        if (relative.getAxis().isVertical()) return relative;
        BlockState state = getBlockState();
        if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return relative;

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction absolute = relative;
        for (Direction d = Direction.NORTH; d != facing; d = d.getClockWise()) {
            absolute = absolute.getClockWise();
        }
        return absolute;
    }
}