package me.samuelh2005.logic_plus_plus.content.value_source;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ValueSourceBlock extends BaseEntityBlock {
    public static final MapCodec<ValueSourceBlock> CODEC = simpleCodec(ValueSourceBlock::new);

    public ValueSourceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ValueSourceBlockEntity(pos, state);
    }

    // BaseEntityBlock defaults to INVISIBLE, so this is required
    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }


    // ---- events: the block only forwards, the block entity does the work ----

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ValueSourceBlockEntity counter) {
            return Math.min(counter.getValue(), 15);
        }
        return 0;
    }
}