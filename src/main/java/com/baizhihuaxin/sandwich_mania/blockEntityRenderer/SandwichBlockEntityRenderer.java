package com.baizhihuaxin.sandwich_mania.blockEntityRenderer;

import com.baizhihuaxin.sandwich_mania.blockEntity.SandwichBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

public class SandwichBlockEntityRenderer implements BlockEntityRenderer<SandwichBlockEntity> {
    private final ItemRenderer itemRenderer;
    public SandwichBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }
    @Override
    public void render(SandwichBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        List<ItemStack> foodItems = entity.getFoodItems();
        if (foodItems.isEmpty()) {
            return;
        }

        matrices.push();

        // 移动到方块中心
        matrices.translate(0.5, 0, 0.5);

        // 渲染每一层食物
        for (int i = 0; i < foodItems.size(); i++) {
            ItemStack food = foodItems.get(i);
            if (!food.isEmpty()) {
                matrices.push();

                // 每层稍微提高一点
                double layerHeight = 0.05 + (i * 0.04);
                matrices.translate(0, layerHeight, 0);

                // 平放在地面上
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

                // 稍微缩小一点
                matrices.scale(0.7f, 0.7f, 0.7f);

                // 每层稍微旋转一点角度，看起来更自然
                if (!food.isOf(Items.BREAD)) {
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * 15f));
                }

                // 渲染物品
                itemRenderer.renderItem(food, ModelTransformationMode.FIXED,light,overlay,matrices,vertexConsumers,entity.getWorld(),0);

                matrices.pop();
            }
        }

        matrices.pop();
    }
}
