package me.samuelh2005.logic_plus_plus.content.digital_display;

import org.jetbrains.annotations.Nullable;

import me.samuelh2005.logic_plus_plus.data.DataBlockEntity;
import me.samuelh2005.logic_plus_plus.data.DataValueInput;
import me.samuelh2005.logic_plus_plus.data.DataValueOutput;
import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalDisplayBlockEntity extends DataBlockEntity {
    private static final Direction BACK = Direction.SOUTH;   // data input
    private final DigitalDisplayBlockEntityDataValueInput BACK_INPUT;
    private int value;

    public DigitalDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIGITAL_DISPLAY.get(), pos, state);
        BACK_INPUT = new DigitalDisplayBlockEntityDataValueInput(this);
    }

    public int getValue() {
        return value;
    }

    @Nullable
    @Override
    public DataValueOutput getDataOutput(@Nullable Direction side) {
        return null;
    }

    @Nullable
    @Override
    public DataValueInput getDataInput(@Nullable Direction side) {
        return side == toAbsolute(BACK) ? BACK_INPUT : null;
    }

    public void onInputChanged() {
        value = readValueAt(toAbsolute(BACK));
    }

    public static class DigitalDisplayBlockEntityDataValueInput implements DataValueInput {
        private final DigitalDisplayBlockEntity blockEntity;

        public DigitalDisplayBlockEntityDataValueInput(DigitalDisplayBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public void onInputChanged() {
            blockEntity.onInputChanged();
        }
    }
}