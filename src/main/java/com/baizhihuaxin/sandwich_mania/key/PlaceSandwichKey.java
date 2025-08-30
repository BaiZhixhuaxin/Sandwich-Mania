package com.baizhihuaxin.sandwich_mania.key;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PlaceSandwichKey {
    public static final String KEY_CATEGORY = "key.category.better-dropping";
    public static final String KEY_PLACE_SANDWICH = "key.element_things.place_sandwich";
    public static KeyBinding placeSandwich;
    public static void registerKeyInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
        });
    }
    public static void register(){
        placeSandwich = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_PLACE_SANDWICH,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                KEY_CATEGORY
        ));
        registerKeyInputs();
    }
    public static boolean isKeyPressed(){
        return placeSandwich.isPressed();
    }
}
