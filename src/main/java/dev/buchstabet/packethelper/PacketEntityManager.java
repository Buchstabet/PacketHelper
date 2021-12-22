package dev.buchstabet.packethelper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PacketEntityManager extends ArrayList<SpawnableDestroyable> implements Consumer<JavaPlugin> {

    public void register(SpawnableDestroyable spawnableDestroyable) {
        add(spawnableDestroyable);
    }

    public void unregister(SpawnableDestroyable spawnableDestroyable) {
        remove(spawnableDestroyable);
    }


    @Override
    public void accept(JavaPlugin javaPlugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(javaPlugin, () -> Bukkit.getOnlinePlayers().forEach(player -> forEach(spawnableDestroyable -> {
            if(!(player.getWorld().equals(spawnableDestroyable.getLocation().getWorld()))) return;
            if (spawnableDestroyable.getLocation().distance(player.getLocation()) > 20 && spawnableDestroyable.contains(player)) {
                spawnableDestroyable.remove(player);
                spawnableDestroyable.destroy(player);
            } else if(spawnableDestroyable.getLocation().distance(player.getLocation()) < 20 && !spawnableDestroyable.contains(player)) {
                spawnableDestroyable.add(player);
                spawnableDestroyable.spawn(player);
            }

            if (spawnableDestroyable instanceof Lookable) {
                Lookable lookable = (Lookable) spawnableDestroyable;
                if (lookable.isLooking()) {
                    lookable.look(player);
                }
            }
        })), 10, 5);
    }
}
