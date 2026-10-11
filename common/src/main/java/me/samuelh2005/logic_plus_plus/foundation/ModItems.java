package me.samuelh2005.logic_plus_plus.foundation;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(LogicPlusPlus.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> COUNTER = ITEMS.register("counter",
            () -> new BlockItem(ModBlocks.COUNTER.get(),
                    new Item.Properties().arch$tab(ModCreativeTabs.MAIN)));
    
    public static final RegistrySupplier<Item> DATA_BUS = ITEMS.register("data_bus",
            () -> new BlockItem(ModBlocks.DATA_BUS.get(),
                    new Item.Properties().arch$tab(ModCreativeTabs.MAIN)));

    public static void register() { ITEMS.register(); }
}