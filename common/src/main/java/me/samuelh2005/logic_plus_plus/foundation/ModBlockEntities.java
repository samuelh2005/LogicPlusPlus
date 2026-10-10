package me.samuelh2005.logic_plus_plus.foundation;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import me.samuelh2005.logic_plus_plus.content.example_block.ExampleBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(LogicPlusPlus.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<ExampleBlockEntity>> EXAMPLE =
            BLOCK_ENTITIES.register("example",
                    () -> BlockEntityType.Builder.of(ExampleBlockEntity::new,
                            ModBlocks.EXAMPLE_BLOCK.get()).build(null));

    public static void register() { BLOCK_ENTITIES.register(); }
}