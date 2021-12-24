package dev.buchstabet.packethelper.implementation;

import dev.buchstabet.packethelper.property.Clickable;
import dev.buchstabet.packethelper.property.Equipable;
import dev.buchstabet.packethelper.property.Lookable;
import dev.buchstabet.packethelper.property.PacketEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class PacketAnimal<V extends EntityInsentient> extends ArrayList<UUID> implements PacketEntity<V>, Clickable<V>, Lookable<V>, Equipable<V> {

  private V entity = null;
  private final Consumer<V> config;
  private final Location location;
  private final boolean looking;
  private final Class<V> clazz;
  private final JavaPlugin plugin;
  @Nullable
  @Getter
  private final Function<Player, Boolean> allowed;

  public PacketAnimal(Consumer<V> config, Location location, boolean looking, Class<V> clazz, JavaPlugin plugin) {
    this(config, location, looking, clazz, plugin, null);
  }

  public PacketAnimal(Location location, boolean looking, Class<V> clazz, JavaPlugin plugin) {
    this(v -> {
    }, location, looking, clazz, plugin, null);
  }

  @Override
  public void run() {
    World world = ((CraftWorld) location.getWorld()).getHandle();
    try {
      Constructor<V> constructor = clazz.getConstructor(World.class);
      entity = constructor.newInstance(world);
      config.accept(entity);
      setLocation(location);
    } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
