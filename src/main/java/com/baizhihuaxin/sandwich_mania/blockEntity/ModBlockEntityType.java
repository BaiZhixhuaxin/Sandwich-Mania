package com.baizhihuaxin.sandwich_mania.blockEntity;

import com.baizhihuaxin.sandwich_mania.SandwichMania;
import com.baizhihuaxin.sandwich_mania.block.ModBlocks;
import com.mojang.datafixers.types.Type;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ModBlockEntityType {
    public static final BlockEntityType<SandwichBlockEntity> SANDWICH_BLOCK_ENTITY = create("sandwich_block",BlockEntityType.Builder.create(SandwichBlockEntity::new, ModBlocks.SANDWICH_BLOCK));

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.Builder<T> builder) {
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(SandwichMania.MOD_ID,id), builder.build(type));
    }
    public static void init(){}
}
