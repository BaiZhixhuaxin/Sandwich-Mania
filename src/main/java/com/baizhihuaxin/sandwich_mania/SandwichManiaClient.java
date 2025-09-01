package com.baizhihuaxin.sandwich_mania;

import com.baizhihuaxin.sandwich_mania.blockEntity.ModBlockEntityType;
import com.baizhihuaxin.sandwich_mania.blockEntityRenderer.SandwichBlockEntityRenderer;
import com.baizhihuaxin.sandwich_mania.key.PlaceSandwichKey;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class SandwichManiaClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntityType.SANDWICH_BLOCK_ENTITY,
                SandwichBlockEntityRenderer::new);
        PlaceSandwichKey.register();
    }
}
