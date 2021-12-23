package dev.buchstabet.packethelper;

import dev.buchstabet.packethelper.implementation.PacketAnimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PacketEntityManager extends ArrayList<PacketEntity<? extends EntityLiving>>
    implements Listener {

  private int viewDistance = 40;

  public static PacketEntityManager create(JavaPlugin javaPlugin) {
    return new PacketEntityManager().start(javaPlugin);
  }

  public static PacketEntityManager create(JavaPlugin javaPlugin, int viewDistance) {
    return new PacketEntityManager(viewDistance).start(javaPlugin);
  }

  public void register(PacketEntity<? extends EntityLiving> packetEntity) {
    add(packetEntity);
  }

  public void unregister(PacketEntity<? extends EntityLiving> packetEntity) {
    remove(packetEntity);
  }

  private PacketEntityManager start(JavaPlugin javaPlugin) {
    javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
    Bukkit.getScheduler()
        .runTaskTimerAsynchronously(
            javaPlugin,
            () ->
                Bukkit.getOnlinePlayers()
                    .forEach(
                        player ->
                            new ArrayList<>(this)
                                .forEach(
                                    packetEntity -> {
                                      if (!(player
                                          .getWorld()
                                          .equals(packetEntity.getLocation().getWorld()))) return;
                                      if (packetEntity.getLocation().distance(player.getLocation())
                                              > viewDistance
                                          && packetEntity.contains(player.getUniqueId())) {
                                        packetEntity.remove(player.getUniqueId());
                                        packetEntity.destroy(player);
                                      } else if (packetEntity
                                                  .getLocation()
                                                  .distance(player.getLocation())
                                              < viewDistance
                                          && !packetEntity.contains(player.getUniqueId())) {
                                        packetEntity.add(player.getUniqueId());
                                        packetEntity.spawn(player);
                                      }

                                      if (packetEntity instanceof Lookable) {
                                        Lookable<?> lookable = (Lookable<?>) packetEntity;
                                        if (lookable.isLooking()) {
                                          lookable.look(player);
                                        }
                                      }

                                      if (packetEntity instanceof AutoRotatable) {
                                        AutoRotatable<?> autoRotatable =
                                            (AutoRotatable<?>) packetEntity;
                                        Location location = autoRotatable.getLocation();
                                        location.setYaw(location.getYaw() + 1);
                                        autoRotatable.setLocation(location);
                                        autoRotatable.teleport(player);
                                      }
                                    })),
            10,
            1);

    return this;
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    forEach(packetEntity -> packetEntity.remove(e.getPlayer().getUniqueId()));
  }
}
