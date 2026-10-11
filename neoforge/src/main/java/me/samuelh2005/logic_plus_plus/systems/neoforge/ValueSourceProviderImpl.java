package me.samuelh2005.logic_plus_plus.systems.neoforge;

import org.jetbrains.annotations.Nullable;

import me.samuelh2005.logic_plus_plus.systems.ValueSource;
import me.samuelh2005.logic_plus_plus.systems.ValueSourceProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class ValueSourceProviderImpl {
    public static final BlockCapability<ValueSource, @Nullable Direction> VALUE_SOURCE =
        BlockCapability.createSided(ValueSourceProvider.id(), ValueSource.class);

    @Nullable
    public static ValueSource find(Level level, BlockPos pos, @Nullable Direction side) {
        return level.getCapability(VALUE_SOURCE, pos, side);
    }
}
