package dev.buchstabet.packethelper.property;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
public interface PacketEntity<V extends EntityLiving> extends List<UUID>, Runnable {

  @Nullable Function<Player, Boolean> getAllowed();

  default void spawn(Player player) {
    PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
    playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(getEntity()));
    playerConnection.sendPacket(new PacketPlayOutEntityMetadata(getEntity().getId(), getEntity().getDataWatcher(), true));
  }

  default void destroy(Player player) {
    ((CraftPlayer) player)
            .getHandle()
            .playerConnection
            .sendPacket(new PacketPlayOutEntityDestroy(getEntity().getId()));
  }

  V getEntity();

  Location getLocation();
}
