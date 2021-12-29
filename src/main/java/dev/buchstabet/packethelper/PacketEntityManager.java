package dev.buchstabet.packethelper;

import dev.buchstabet.packethelper.property.AutoRotatable;
import dev.buchstabet.packethelper.property.Lookable;
import dev.buchstabet.packethelper.property.PacketEntity;
import dev.buchstabet.packethelper.property.Rotatable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PacketEntityManager extends ArrayList<PacketEntity<? extends EntityLiving>> implements Listener {

  @Getter private final JavaPlugin javaPlugin;

  final List<Player> players = new ArrayList<>();

  public static PacketEntityManager getInstance() {
    return PacketHelperPluginLoader.getPacketEntityManager();
  }

  @SafeVarargs
  public final void register(PacketEntity<? extends EntityLiving>... packetEntities) {
    for (PacketEntity<? extends EntityLiving> packetEntity : packetEntities) {
      packetEntity.run();
      add(packetEntity);
    }
  }

  @SafeVarargs
  public final void unregister(PacketEntity<? extends EntityLiving>... packetEntities) {
    for (PacketEntity<? extends EntityLiving> packetEntity : packetEntities) {
      remove(packetEntity);
      packetEntity.forEach(uuid -> packetEntity.destroy(Bukkit.getPlayer(uuid)));
    }
  }

  void start(int viewDistance) {
    javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
    Bukkit.getScheduler().runTaskTimerAsynchronously(javaPlugin, () -> players.forEach(player -> forEach(packetEntity -> {
      if (!(player.getWorld().equals(packetEntity.getLocation().getWorld()))) {
        packetEntity.remove(player.getUniqueId());
        return;
      }

      if (packetEntity.getAllowed() != null && !packetEntity.getAllowed().apply(player)) {
        if (packetEntity.contains(player.getUniqueId())) {
          packetEntity.destroy(player);
          packetEntity.remove(player.getUniqueId());
        }
        return;
      }

      if (packetEntity.getLocation().distance(player.getLocation()) > viewDistance && packetEntity.contains(player.getUniqueId())) {
        packetEntity.remove(player.getUniqueId());
        packetEntity.destroy(player);
      } else if (packetEntity.getLocation().distance(player.getLocation()) < viewDistance && !packetEntity.contains(player.getUniqueId())) {
        packetEntity.add(player.getUniqueId());
        packetEntity.spawn(player);
      } else {

        if (packetEntity instanceof Lookable) {
          Lookable<?> lookable = (Lookable<?>) packetEntity;
          if (lookable.isLooking()) {
            lookable.look(player);
          }
        }

        if (packetEntity instanceof AutoRotatable) {
          AutoRotatable<?> autoRotatable = (AutoRotatable<?>) packetEntity;
          Location location = autoRotatable.getLocation();
          location.setYaw(location.getYaw() + autoRotatable.getSpeed());
          autoRotatable.setLocation(location);
          autoRotatable.teleport(player);
          if (packetEntity instanceof Rotatable) {
            Rotatable<?> rotatable = (Rotatable<?>) packetEntity;
            rotatable.rotate(player, location.getYaw());
          }
        }
      }
    })), 10, 3);

  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    players.remove(e.getPlayer());
    forEach(packetEntity -> packetEntity.remove(e.getPlayer().getUniqueId()));
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    players.add(e.getPlayer());
  }

  @EventHandler
  public void onDeath(PlayerRespawnEvent e) {
    forEach(packetEntity -> packetEntity.remove(e.getPlayer().getUniqueId()));
  }


}
