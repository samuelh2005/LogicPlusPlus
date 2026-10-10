package me.samuelh2005.logic_plus_plus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.samuelh2005.logic_plus_plus.foundation.ModBlockEntities;
import me.samuelh2005.logic_plus_plus.foundation.ModBlocks;
import me.samuelh2005.logic_plus_plus.foundation.ModCreativeTabs;
import me.samuelh2005.logic_plus_plus.foundation.ModItems;

public final class LogicPlusPlus {
    public static final String MOD_ID = "logic_plus_plus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        ModBlocks.register();
        ModBlockEntities.register();
        ModCreativeTabs.register();
        ModItems.register();

        LOGGER.info("Logic++ has been initialized!");
    }
}
