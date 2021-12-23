package dev.buchstabet.packethelper.implementation;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.buchstabet.packethelper.AutoRotatable;
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

@Getter
public class RotatingHead extends FlyingItem implements AutoRotatable<EntityArmorStand> {

  private final float speed;

  public RotatingHead(
      float speed,
      Location location,
      @Nullable Function<Player, String> nameFunction,
      String headValue) {
    super(createHead(headValue), nameFunction);
    this.speed = speed;
    create(location);
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
