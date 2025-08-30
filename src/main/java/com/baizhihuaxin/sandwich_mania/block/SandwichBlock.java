package com.baizhihuaxin.sandwich_mania.block;

import com.baizhihuaxin.sandwich_mania.blockEntity.SandwichBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SandwichBlock extends BlockWithEntity {
    public SandwichBlock(Settings settings) {
        super(settings);
    }
    public static final MapCodec<SandwichBlock> CODEC = createCodec(SandwichBlock::new);

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SandwichBlockEntity(pos,state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!world.isClient) {
            // 确保方块实体被正确创建
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SandwichBlockEntity) {
                blockEntity.markDirty();
            }
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SandwichBlockEntity sandwich) {
            int layers = sandwich.getFoodItems().size();
            double height = Math.min(0.8, layers * 0.05 + 0.1); // 每层增加0.1高度，最大0.8
            return VoxelShapes.cuboid(0.2, 0, 0.2, 0.8, height, 0.8);
        }
        return VoxelShapes.cuboid(0.2, 0, 0.2, 0.8, 0.2, 0.8);
    }
}
