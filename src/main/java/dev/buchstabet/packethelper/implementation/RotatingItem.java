package dev.buchstabet.packethelper.implementation;

import dev.buchstabet.packethelper.property.AutoRotatable;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/

public class RotatingItem extends FlyingItem implements AutoRotatable<EntityArmorStand> {

    @Getter private final float speed;

    public RotatingItem(Location location, ItemStack item, @Nullable Function<Player, String> nameFunction, @Nullable Function<Player, Boolean> allowed, float speed) {
        super(location, item, nameFunction, allowed);
        this.speed = speed;
    }

    public RotatingItem(Location location, ItemStack value, float speed) {
        this(location, value, null, null, speed);
    }

    public RotatingItem(Location location, ItemStack value) {
        this(location, value, null, null, 5F);
    }

}
