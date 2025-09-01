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

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.init();
		ModBlockEntityType.init();
		UseBlockCallback.EVENT.register(new SandwichEventHandler());
	}
}