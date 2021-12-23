package dev.buchstabet.packethelper.implementation;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.buchstabet.packethelper.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.AchievementList;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class RotatingHead extends ArrayList<UUID>
    implements PacketEntity<EntityArmorStand>, AutoRotatable<EntityArmorStand>, Equipable<EntityArmorStand> {

  private final String headValue;
  private EntityArmorStand entity;
  private Location location;

  public RotatingHead create(Location location) {
    World world = ((CraftWorld) location.getWorld()).getHandle();
    entity = new EntityArmorStand(world);
    setLocation(location);

    entity.setInvisible(true);
    return this;
  }

  @Override
  public void spawn(Player player) {
    PacketEntity.super.spawn(player);
    equip(player, 4, createHead());
  }

  @Override
  public void setLocation(Location location) {
    this.location = location;
    entity.setLocation(
        location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
  }

  private ItemStack createHead() {
    ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
    if (headValue.isEmpty()) return head;

    SkullMeta headMeta = (SkullMeta) head.getItemMeta();
    GameProfile profile = new GameProfile(UUID.randomUUID(), null);

    profile.getProperties().put("textures", new Property("textures", headValue));

    try {
      Field profileField = headMeta.getClass().getDeclaredField("profile");
      profileField.setAccessible(true);
      profileField.set(headMeta, profile);

    } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
      error.printStackTrace();
    }
    head.setItemMeta(headMeta);
    return head;
  }
}
