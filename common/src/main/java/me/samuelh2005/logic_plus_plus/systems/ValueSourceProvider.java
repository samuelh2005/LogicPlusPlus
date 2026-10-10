package me.samuelh2005.logic_plus_plus.systems;

import org.jetbrains.annotations.Nullable;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ValueSourceProvider {
    @Nullable
    @ExpectPlatform
    public static ValueSource find(Level level, BlockPos pos, @Nullable Direction side) {
        throw new AssertionError();
    }

    // Registration is also platform-specific
    public static ResourceLocation id() {
        return ResourceLocation.fromNamespaceAndPath(LogicPlusPlus.MOD_ID, "value_source");
    }
}
