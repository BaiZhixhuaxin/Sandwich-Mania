package com.baizhihuaxin.sandwich_mania.block;

import com.baizhihuaxin.sandwich_mania.SandwichMania;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block SANDWICH_BLOCK = register("sandwich_block", new SandwichBlock(AbstractBlock.Settings.copy(Blocks.OAK_BUTTON).noBlockBreakParticles().strength(-1.0F, 2.0F)));
    public static void registerBlockItem(String id,Block block){
        BlockItem item =  Registry.register(Registries.ITEM, Identifier.of(SandwichMania.MOD_ID,id),new BlockItem(block,new Item.Settings()));
        item.appendBlocks(Item.BLOCK_ITEMS,item);
    }


    public static Block register(String id, Block block) {
        registerBlockItem(id,block);
        return Registry.register(Registries.BLOCK, Identifier.of(SandwichMania.MOD_ID,id), block);
    }
    public static void init(){}
}
