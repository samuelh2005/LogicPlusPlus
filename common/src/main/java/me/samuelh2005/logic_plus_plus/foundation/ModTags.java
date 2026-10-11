package me.samuelh2005.logic_plus_plus.foundation;

import me.samuelh2005.logic_plus_plus.LogicPlusPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    public static final TagKey<Block> DIGITAL_BLOCKS = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(LogicPlusPlus.MOD_ID, "digital_blocks"));
}