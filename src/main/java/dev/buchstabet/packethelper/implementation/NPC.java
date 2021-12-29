package dev.buchstabet.packethelper.implementation;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.buchstabet.packethelper.PacketHelperPluginLoader;
import dev.buchstabet.packethelper.property.Clickable;
import dev.buchstabet.packethelper.property.Equipable;
import dev.buchstabet.packethelper.property.Lookable;
import dev.buchstabet.packethelper.property.PacketEntity;
import dev.buchstabet.packethelper.utils.FieldManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/********************************************
 * Copyright (c) by Konstantin Krötz
 *******************************************/
@Getter
@RequiredArgsConstructor
public class NPC extends ArrayList<UUID> implements PacketEntity<EntityPlayer>, Lookable<EntityPlayer>, Clickable<EntityPlayer>, Equipable<EntityPlayer>, Listener {

  private final Location location;
  @Nullable private final Property skinData;
  private GameProfile gameProfile;
  private EntityPlayer entity;
  @Nullable
  @Getter
  private final Function<Player, Boolean> allowed;
  private final boolean looking;
  @Nullable private final Function<Player, String> nameFunction;

  public NPC(Location location, boolean looking) {
    this(location, null, null, looking, null);
  }

  public NPC(Location location, boolean looking, Function<Player, String> nameFunction) {
    this(location, null, null, looking, nameFunction);
  }

  public NPC(Location location, Property skinData, boolean looking, Function<Player, String> nameFunction) {
    this(location, skinData, null, looking, nameFunction);
  }

  public NPC(Location location, Property property, boolean looking) {
    this(location, property, null, looking, null);
  }

  public NPC(Location location, String[] property, boolean looking) {
    this(location, new Property("textures", property[0], property[1]), null, looking, null);
  }

  public void spawn(Player player) {
    if (nameFunction != null)
      new FieldManager().setValue(gameProfile, "name", nameFunction.apply(player));
    if (skinData == null) {
      gameProfile.getProperties().removeAll("textures");
      gameProfile.getProperties().putAll("textures", ((CraftPlayer) player).getProfile().getProperties().get("textures"));
    }

    PlayerConnection b = ((CraftPlayer) player).getHandle().playerConnection;

    b.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity));
    PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(entity);

    DataWatcher dataWatcher = entity.getDataWatcher();
    dataWatcher.watch(10, (byte) 0xFF);

    b.sendPacket(packetPlayOutNamedEntitySpawn);
    teleport(player);
    rotate(player, location.getYaw());

    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(PacketHelperPluginLoader.class), () -> {
      PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity);
      PacketPlayOutPlayerInfo.PlayerInfoData data = packetPlayOutPlayerInfo.new PlayerInfoData(gameProfile, 1, WorldSettings.EnumGamemode.NOT_SET, CraftChatMessage.fromString(entity.getProfile().getName())[0]);

      List<PacketPlayOutPlayerInfo.PlayerInfoData> players = new FieldManager().getValue(List.class, packetPlayOutPlayerInfo, "b");
      players.add(data);
      new FieldManager().setValue(packetPlayOutPlayerInfo, "b", players);
      b.sendPacket(packetPlayOutPlayerInfo);
    }, 20);
  }

  @Override
  public void run() {
    gameProfile = new GameProfile(UUID.randomUUID(), "");
    if (skinData != null) {
      gameProfile.getProperties().removeAll("textures");
      gameProfile.getProperties().put("textures", skinData);
    }

    MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
    WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();

    entity = new EntityPlayer(minecraftServer, worldServer, gameProfile, new DemoPlayerInteractManager(worldServer));
    entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
  }
}
