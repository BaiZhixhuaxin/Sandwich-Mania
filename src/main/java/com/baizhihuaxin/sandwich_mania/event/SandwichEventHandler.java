package com.baizhihuaxin.sandwich_mania.event;

import com.baizhihuaxin.sandwich_mania.block.ModBlocks;
import com.baizhihuaxin.sandwich_mania.blockEntity.SandwichBlockEntity;
import com.baizhihuaxin.sandwich_mania.key.PlaceSandwichKey;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SandwichEventHandler implements UseBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient || hand != Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }

        ItemStack heldItem = player.getStackInHand(hand);
        BlockPos pos = hitResult.getBlockPos();

        // 检查是否按下了绑定键并且手持食物
        if (PlaceSandwichKey.isKeyPressed() && heldItem.getComponents().contains(DataComponentTypes.FOOD)) {
            // 检查方块上方是否为空
            BlockPos abovePos = pos.up();
            if (world.getBlockState(abovePos).isAir()) {
                if (world.getBlockState(pos).getBlock().equals(ModBlocks.SANDWICH_BLOCK)) {
                    if (world.getBlockEntity(pos) instanceof SandwichBlockEntity sandwichBlockEntity) {
                        ItemStack stack = heldItem.copy();
                        stack.setCount(1);
                        sandwichBlockEntity.addFoodItem(stack);
                        // 消耗物品（创造模式不消耗）
                        if (!player.getAbilities().creativeMode) {
                            heldItem.decrement(1);
                        }
                    }
                    return ActionResult.SUCCESS;
                }
                // 创建三明治方块
                world.setBlockState(abovePos, ModBlocks.SANDWICH_BLOCK.getDefaultState());

                // 获取方块实体并添加食物
                if (world.getBlockEntity(abovePos) instanceof SandwichBlockEntity sandwich) {
                    ItemStack stack = heldItem.copy();
                    stack.setCount(1);
                    sandwich.addFoodItem(stack);

                    // 消耗物品（创造模式不消耗）
                    if (!player.getAbilities().creativeMode) {
                        heldItem.decrement(1);
                    }

                    return ActionResult.SUCCESS;
                }
            }
            //按绑定按键空手拆散三明治
        } else if (PlaceSandwichKey.isKeyPressed() && player.getMainHandStack().equals(ItemStack.EMPTY)) {
            if (world.getBlockState(pos).getBlock().equals(ModBlocks.SANDWICH_BLOCK)) {
                if (world.getBlockEntity(pos) instanceof SandwichBlockEntity sandwichBlockEntity) {
                    for (ItemStack stack : sandwichBlockEntity.getFoodItems()) {
                        ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                        world.spawnEntity(item);
                    }
                    ItemStackParticleEffect particleEffect = new ItemStackParticleEffect(
                            ParticleTypes.ITEM, // 粒子类型
                            sandwichBlockEntity.getFoodItems().getFirst()            // 物品栈
                    );
                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(particleEffect, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.02);
                    }
                }
                world.breakBlock(pos, false);
                return ActionResult.SUCCESS;
            }
            //潜行空手右键吃三明治
        } else if (player.isSneaking() && player.getMainHandStack().equals(ItemStack.EMPTY)) {
            if (world.getBlockState(pos).getBlock().equals(ModBlocks.SANDWICH_BLOCK)) {
                if (world.getBlockEntity(pos) instanceof SandwichBlockEntity sandwichBlockEntity) {
                    List<StatusEffectInstance> effects = new ArrayList<>();
                    for (ItemStack food : sandwichBlockEntity.getFoodItems()) {
                        ItemStack leftStack = food.getRecipeRemainder();
                        Optional<RecipeEntry<CraftingRecipe>> craftingRecipeRecipeEntry = findMatchingRecipe(player,food);
                        if (craftingRecipeRecipeEntry.isPresent()) {
                            CraftingRecipe recipe = craftingRecipeRecipeEntry.get().value();
                                List<ItemStack> stacks = parseIngredients(recipe);
                                for(ItemStack stack : stacks) {
                                    if(stack.isOf(Items.BOWL)) world.spawnEntity(new ItemEntity(world,pos.getX(),pos.getY(),pos.getZ(),new ItemStack(Items.BOWL,1)));
                            }
                        }
                        ItemEntity item = new ItemEntity(world,pos.getX(),pos.getY(),pos.getZ(),leftStack);
                        if(food.isOf(Items.SUSPICIOUS_STEW)) world.spawnEntity(new ItemEntity(world,pos.getX(),pos.getY(),pos.getZ(),new ItemStack(Items.BOWL,1)));
                        world.spawnEntity(item);
                        player.getHungerManager().eat(food.getComponents().get(DataComponentTypes.FOOD));
                        FoodComponent foodComp = food.getComponents().get(DataComponentTypes.FOOD);
                        if(food.getItem() instanceof SuspiciousStewItem){
                            for(SuspiciousStewEffectsComponent.StewEffect stewEffect : food.getComponents().get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS).effects()){
                                effects.add(new StatusEffectInstance(stewEffect.effect(),stewEffect.duration()));
                            }
                        }
                        else foodComp.effects().forEach(effect -> {
                            if (world.random.nextFloat() < effect.probability()) {
                                effects.add(new StatusEffectInstance(effect.effect()));
                            }
                        });
                    }
                    for (StatusEffectInstance effect : effects) {
                        player.addStatusEffect(effect);
                    }
                    ItemStackParticleEffect particleEffect = new ItemStackParticleEffect(
                            ParticleTypes.ITEM, // 粒子类型
                            sandwichBlockEntity.getFoodItems().getFirst()            // 物品栈
                    );
                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(particleEffect, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.02);
                        serverWorld.setBlockState(pos, Blocks.AIR.getDefaultState());
                        serverWorld.playSound(null,pos,SoundEvents.ENTITY_GENERIC_EAT,SoundCategory.PLAYERS,1.0f,1.0f);
                        System.out.println("play sound");
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
    private Optional<RecipeEntry<CraftingRecipe>> findMatchingRecipe(PlayerEntity player, ItemStack stack){
        MinecraftServer server = player.getServer();
        RegistryWrapper.WrapperLookup lookup = player.getRegistryManager();
        if(server == null) return Optional.empty();
        RecipeManager recipeManager = server.getRecipeManager();
        return (recipeManager.listAllOfType(RecipeType.CRAFTING).stream().filter(recipe -> {
            ItemStack output = recipe.value().getResult(lookup);
            return output.getItem() == stack.getItem() && output.getCount() == 1;
        })).findFirst();
    }
    private List<ItemStack> parseIngredients(CraftingRecipe recipe){
        return recipe.getIngredients().stream().map(ingredient -> ingredient.getMatchingStacks().length > 0 ?
                        ingredient.getMatchingStacks()[0].copy() :
                        ItemStack.EMPTY)
                .filter(stack -> !stack.isEmpty())
                .peek(stack -> stack.setCount(1))
                .collect(Collectors.toList());
    }
}
