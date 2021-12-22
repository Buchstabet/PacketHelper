package dev.buchstabet.packethelper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PacketEntityManager extends ArrayList<SpawnableDestroyable<? extends EntityLiving>>
    implements Listener {

  public static PacketEntityManager create(JavaPlugin javaPlugin) {
    return new PacketEntityManager().start(javaPlugin);
  }

  public void register(SpawnableDestroyable<? extends EntityLiving> spawnableDestroyable) {
    add(spawnableDestroyable);
  }

  public void unregister(SpawnableDestroyable<? extends EntityLiving> spawnableDestroyable) {
    remove(spawnableDestroyable);
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
                            forEach(
                                spawnableDestroyable -> {
                                  if (!(player
                                      .getWorld()
                                      .equals(spawnableDestroyable.getLocation().getWorld())))
                                    return;
                                  if (spawnableDestroyable
                                              .getLocation()
                                              .distance(player.getLocation())
                                          > 20
                                      && spawnableDestroyable.contains(player.getUniqueId())) {
                                    spawnableDestroyable.remove(player.getUniqueId());
                                    spawnableDestroyable.destroy(player);
                                  } else if (spawnableDestroyable
                                              .getLocation()
                                              .distance(player.getLocation())
                                          < 20
                                      && !spawnableDestroyable.contains(player)) {
                                    spawnableDestroyable.add(player.getUniqueId());
                                    spawnableDestroyable.spawn(player);
                                  }

                                  if (spawnableDestroyable instanceof Lookable) {
                                    Lookable<?> lookable = (Lookable<?>) spawnableDestroyable;
                                    if (lookable.isLooking()) {
                                      lookable.look(player);
                                    }
                                  }
                                })),
            10,
            5);

    return this;
  }
}
