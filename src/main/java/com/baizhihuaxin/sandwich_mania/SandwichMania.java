package com.baizhihuaxin.sandwich_mania;

import com.baizhihuaxin.sandwich_mania.block.ModBlocks;
import com.baizhihuaxin.sandwich_mania.blockEntity.ModBlockEntityType;
import com.baizhihuaxin.sandwich_mania.event.SandwichEventHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SandwichMania implements ModInitializer {
	public static final String MOD_ID = "sandwich_mania";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		ModBlocks.init();
		ModBlockEntityType.init();
		UseBlockCallback.EVENT.register(new SandwichEventHandler());
	}
}