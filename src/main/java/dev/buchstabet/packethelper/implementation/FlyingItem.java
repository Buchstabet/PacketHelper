package dev.buchstabet.packethelper.implementation;

import dev.buchstabet.packethelper.property.Equipable;
import dev.buchstabet.packethelper.property.PacketEntity;
import dev.buchstabet.packethelper.property.Teleportable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
@Getter
@RequiredArgsConstructor
public class FlyingItem extends ArrayList<UUID> implements PacketEntity<EntityArmorStand>, Teleportable<EntityArmorStand>, Equipable<EntityArmorStand> {

  private EntityArmorStand entity;
  private final Location location;
  private final ItemStack stack;
  @Nullable private final Function<Player, String> nameFunction;
  @Nullable private final Function<Player, Boolean> allowed;

  public FlyingItem(Location location, ItemStack stack) {
    this(location, stack, null, null);
  }

  @Override
  public void run() {
    World world = ((CraftWorld) location.getWorld()).getHandle();
    entity = new EntityArmorStand(world);
    entity.setInvisible(true);
    changeEntityLocation(location);

    if (nameFunction != null) {
      entity.setCustomNameVisible(true);
    }
  }

  @Override
  public void spawn(Player player) {
    if (nameFunction != null) entity.setCustomName(nameFunction.apply(player));
    PacketEntity.super.spawn(player);
    equip(player, 4, stack);
  }
}
