package me.samuelh2005.logic_plus_plus.foundation;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import me.samuelh2005.logic_plus_plus.content.counter.CounterBlock;
import me.samuelh2005.logic_plus_plus.content.digital_cable.DigitalCableBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(LogicPlusPlus.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<Block> COUNTER = BLOCKS.register("counter",
            () -> new CounterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .requiresCorrectToolForDrops()));
    
    public static final RegistrySupplier<Block> DIGITAL_CABLE = BLOCKS.register("digital_cable",
            () -> new DigitalCableBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .requiresCorrectToolForDrops()));

    public static void register() { BLOCKS.register(); }
}