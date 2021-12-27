package dev.buchstabet.packethelper.implementation;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.buchstabet.packethelper.property.AutoRotatable;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Function;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
@Getter
public class RotatingHead extends FlyingItem implements AutoRotatable<EntityArmorStand> {

    private final float speed;

    public RotatingHead(Location location, String value, @Nullable Function<Player, String> nameFunction, @Nullable Function<Player, Boolean> allowed, float speed) {
        super(location, createHead(value), nameFunction, allowed);
        this.speed = speed;
    }

    public RotatingHead(Location location, String value, float speed) {
        this(location, value, null, null, speed);
    }

    public RotatingHead(Location location, String value) {
        this(location, value, null, null, 5F);
    }

    private static ItemStack createHead(String headValue) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (headValue.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", headValue));

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);

        } catch (IllegalArgumentException
                | NoSuchFieldException
                | SecurityException
                | IllegalAccessException error) {
            error.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
}
