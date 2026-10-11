package me.samuelh2005.logic_plus_plus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import me.samuelh2005.logic_plus_plus.content.digital_display.DigitalDisplayRenderer;
import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;
import me.samuelh2005.logic_plus_plus.foundation.ModBlocks;
import me.samuelh2005.logic_plus_plus.foundation.ModClientNetworking;
import me.samuelh2005.logic_plus_plus.foundation.ModCreativeTabs;
import me.samuelh2005.logic_plus_plus.foundation.ModItems;
import me.samuelh2005.logic_plus_plus.foundation.ModNetworking;

public final class LogicPlusPlus {
    public static final String MOD_ID = "logic_plus_plus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        ModBlocks.register();
        ModBlockEntities.register();
        ModCreativeTabs.register();
        ModItems.register();
        ModNetworking.register();

        LOGGER.info("Logic Plus Plus has been initialized!");
    }

    public static void initClient() {
        ModClientNetworking.register();
        BlockEntityRendererRegistry.register(ModBlockEntities.DIGITAL_DISPLAY.get(), DigitalDisplayRenderer::new);
    }
}
