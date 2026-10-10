package me.samuelh2005.logic_plus_plus.foundation;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import me.samuelh2005.logic_plus_plus.content.counter.CounterBlockEntity;
import me.samuelh2005.logic_plus_plus.content.digital_cable.DigitalCableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(LogicPlusPlus.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<CounterBlockEntity>> COUNTER =
            BLOCK_ENTITIES.register("counter",
                    () -> BlockEntityType.Builder.of(CounterBlockEntity::new,
                            ModBlocks.COUNTER.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<DigitalCableBlockEntity>> DIGITAL_CABLE =
            BLOCK_ENTITIES.register("digital_cable",
                    () -> BlockEntityType.Builder.of(DigitalCableBlockEntity::new,
                            ModBlocks.DIGITAL_CABLE.get()).build(null));

    public static void register() { BLOCK_ENTITIES.register(); }
}