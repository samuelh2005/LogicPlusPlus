package me.samuelh2005.logic_plus_plus.foundation;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import me.samuelh2005.logic_plus_plus.content.counter.CounterBlockEntity;
import me.samuelh2005.logic_plus_plus.content.data_bus.DataBusBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(LogicPlusPlus.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<CounterBlockEntity>> COUNTER =
            BLOCK_ENTITIES.register("counter",
                    () -> BlockEntityType.Builder.of(CounterBlockEntity::new,
                            ModBlocks.COUNTER.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<DataBusBlockEntity>> DATA_BUS =
            BLOCK_ENTITIES.register("data_bus",
                    () -> BlockEntityType.Builder.of(DataBusBlockEntity::new,
                            ModBlocks.DATA_BUS.get()).build(null));

    public static void register() { BLOCK_ENTITIES.register(); }
}