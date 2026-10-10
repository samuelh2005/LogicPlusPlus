package me.samuelh2005.logic_plus_plus.fabric.systems;

import org.jetbrains.annotations.Nullable;

import me.samuelh2005.logic_plus_plus.systems.ValueSource;
import me.samuelh2005.logic_plus_plus.systems.ValueSourceProvider;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class ValueSourceProviderImpl {
    public static final BlockApiLookup<ValueSource, @Nullable Direction> VALUE_SOURCE =
        BlockApiLookup.get(ValueSourceProvider.id(), ValueSource.class, Direction.class);

    @Nullable
    public static ValueSource find(Level level, BlockPos pos, @Nullable Direction side) {
        return VALUE_SOURCE.find(level, pos, side);
    }
}
