package dev.buchstabet.packethelper;

import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface PacketEntity<V extends EntityLiving> extends List<UUID> {

  void spawn(Player player);

  void destroy(Player player);

  Location getLocation();

  V getEntity();

}
