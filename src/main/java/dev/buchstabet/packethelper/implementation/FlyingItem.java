package dev.buchstabet.packethelper.implementation;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.buchstabet.packethelper.AutoRotatable;
import dev.buchstabet.packethelper.Equipable;
import dev.buchstabet.packethelper.PacketEntity;
import dev.buchstabet.packethelper.Teleportable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class FlyingItem extends ArrayList<UUID>
    implements PacketEntity<EntityArmorStand>,
        Teleportable<EntityArmorStand>,
        Equipable<EntityArmorStand> {

  private EntityArmorStand entity;
  private Location location;
  private final ItemStack stack;
  @Nullable private final Function<Player, String> nameFunction;

  public FlyingItem create(Location location) {
    World world = ((CraftWorld) location.getWorld()).getHandle();
    entity = new EntityArmorStand(world);
    setLocation(location);
    entity.setInvisible(true);

    if (nameFunction != null) {
      entity.setCustomNameVisible(true);
    }
    return this;
  }

  @Override
  public void spawn(Player player) {
    if (nameFunction != null) entity.setCustomName(nameFunction.apply(player));
    PacketEntity.super.spawn(player);
    equip(player, 4, stack);
  }

  @Override
  public void setLocation(Location location) {
    this.location = location;
    entity.setLocation(
        location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
  }
}
