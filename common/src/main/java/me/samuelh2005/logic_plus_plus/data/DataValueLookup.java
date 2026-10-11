package me.samuelh2005.logic_plus_plus.data;

import org.jetbrains.annotations.Nullable;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class DataValueLookup {
    /** @param side the face of the block at pos being queried */
    @Nullable
    @ExpectPlatform
    public static DataValueOutput findOutput(Level level, BlockPos pos, @Nullable Direction side) {
        throw new AssertionError();
    }

    /** @param side the face of the block at pos being queried */
    @Nullable
    @ExpectPlatform
    public static DataValueInput findInput(Level level, BlockPos pos, @Nullable Direction side) {
        throw new AssertionError();
    }

    /**
     * THE connection rule: a face is a data connection if, and only if, it exposes a
     * data input or a data output. Redstone-only faces are not connections.
     */
    public static boolean hasConnection(LevelAccessor level, BlockPos pos, Direction side) {
        if (!(level instanceof Level real)) return false;
        return findOutput(real, pos, side) != null || findInput(real, pos, side) != null;
    }

    // Registration is platform-specific
    public static ResourceLocation outputId() {
        return ResourceLocation.fromNamespaceAndPath(LogicPlusPlus.MOD_ID, "data_value_output");
    }

    public static ResourceLocation inputId() {
        return ResourceLocation.fromNamespaceAndPath(LogicPlusPlus.MOD_ID, "data_value_input");
    }
}