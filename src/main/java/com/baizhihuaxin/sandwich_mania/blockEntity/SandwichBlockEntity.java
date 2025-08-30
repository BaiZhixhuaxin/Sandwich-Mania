package com.baizhihuaxin.sandwich_mania.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SandwichBlockEntity extends BlockEntity {
    public SandwichBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.SANDWICH_BLOCK_ENTITY, pos, state);
    }
    private List<ItemStack> foodItems = new ArrayList<>();
    public void addFoodItem(ItemStack food){
        foodItems.add(food);
        markDirty();
        RegistryWrapper.WrapperLookup registryLookup = world.getRegistryManager();
        NbtCompound nbt = new NbtCompound();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            var packet = toUpdatePacket();
            if (packet != null) {
                // 获取附近的玩家并发送更新包
                world.getPlayers().forEach(player -> {
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        if (serverPlayer.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64) {
                            serverPlayer.networkHandler.sendPacket(packet);
                        }
                    }
                });
            }
            world.getChunk(pos).setNeedsSaving(true);
            world.playSound(null,pos, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            this.writeNbt(nbt,registryLookup);
            this.readNbt(nbt,registryLookup);
        }
    }
    public ItemStack removeTopFoodItem(){
        if(!foodItems.isEmpty()){
            ItemStack removed = foodItems.removeLast();
            markDirty();
            if (world != null && !world.isClient) {
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            }
            return removed;
        }
        return ItemStack.EMPTY;
    }
    public List<ItemStack> getFoodItems() {
        return foodItems;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        NbtList foodList = new NbtList();
        for (ItemStack itemStack : foodItems) {
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                foodList.add(itemStack.encode(registryLookup, nbtCompound));
            }
        }
        if (!foodItems.isEmpty()) {
            nbt.put("Items", foodList);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        foodItems.clear();
        NbtList foodList = nbt.getList("Items", 10);
        for (int i = 0; i < foodList.size(); i++) {
            NbtCompound foodNbt = foodList.getCompound(i);
            // 1.21+ 使用新的组件系统解码方法
            Optional<ItemStack> food = ItemStack.fromNbt(registryLookup, foodNbt);
            food.ifPresent(stack -> foodItems.add(stack));
        }
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt,registryLookup);
        return nbt;
    }
}
