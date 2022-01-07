package dev.buchstabet.packethelper.implementation;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/

public class NameablePacketAnimal<V extends EntityInsentient> extends PacketAnimal<V> {

  private final Function<Player, String> nameFunction;

  public NameablePacketAnimal(Consumer<V> config, Location location, boolean looking, Class<V> clazz, @Nullable Function<Player, Boolean> allowed, Function<Player, String> nameFunction) {
    super(config, location, looking, clazz, allowed);
    this.nameFunction = nameFunction;
  }

  public NameablePacketAnimal(Consumer<V> config, Location location, boolean looking, Class<V> clazz, Function<Player, String> nameFunction) {
    super(config, location, looking, clazz);
    this.nameFunction = nameFunction;
  }

  public NameablePacketAnimal(Location location, boolean looking, Class<V> clazz, Function<Player, String> nameFunction) {
    super(location, looking, clazz);
    this.nameFunction = nameFunction;
  }

  @Override
  public void spawn(Player player) {
    if (!getEntity().getCustomNameVisible()) getEntity().setCustomNameVisible(true);
    getEntity().setCustomName(nameFunction.apply(player));
    super.spawn(player);
  }
}
