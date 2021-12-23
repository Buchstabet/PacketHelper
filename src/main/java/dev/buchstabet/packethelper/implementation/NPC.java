package dev.buchstabet.packethelper.implementation;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.buchstabet.packethelper.*;
import lombok.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public interface NPC extends PacketEntity<EntityPlayer>, Lookable<EntityPlayer>, Clickable<EntityPlayer>, Equipable<EntityPlayer> {

  static NPC create(
      Location location,
      boolean looking,
      Function<Player, String> nameFunction,
      JavaPlugin javaPlugin) {
    return create(location, looking, nameFunction, javaPlugin, (Property) null);
  }

  static NPC create(
          Location location,
          boolean looking,
          Function<Player, String> nameFunction,
          JavaPlugin javaPlugin,
          String[] skinData) {
    return create(location, looking, nameFunction, javaPlugin, new Property("textures", skinData[0], skinData[1]));
  }

  static NPC create(
      Location location,
      boolean looking,
      Function<Player, String> nameFunction,
      JavaPlugin javaPlugin,
      Property property) {
    return new NPCImpl(location, looking, nameFunction, javaPlugin, property)
        .buildGameProfile()
        .create();
  }

  @Getter
  class NPCImpl extends ArrayList<UUID> implements NPC {
    private Location location;
    @Nullable Property skinData;
    private GameProfile gameProfile;
    private final String name = "BOB";
    private EntityPlayer entity;

    private final boolean looking;
    private final Function<Player, String> nameFunction;
    private final JavaPlugin javaPlugin;

    private NPCImpl(
        @NotNull Location location,
        boolean looking,
        @NotNull Function<Player, String> nameFunction,
        @NotNull JavaPlugin javaPlugin,
        @Nullable Property property) {
      this.location = location;
      this.looking = looking;
      this.nameFunction = nameFunction;
      this.javaPlugin = javaPlugin;
      this.skinData = property;
    }

    @Override
    public void setLocation(Location location) {
      this.location = location;
      getEntity()
              .setLocation(
                      location.getX(),
                      location.getY(),
                      location.getZ(),
                      location.getYaw(),
                      location.getPitch());
    }

    public NPCImpl buildGameProfile() {
      gameProfile = new GameProfile(UUID.randomUUID(), name);
      if (skinData != null) {
        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", skinData);
      }
      return this;
    }

    public void spawn(Player player) {
      PacketEntity.setValue(gameProfile, "name", nameFunction.apply(player));

      if (skinData == null) {
        gameProfile.getProperties().removeAll("textures");
        gameProfile
            .getProperties()
            .putAll(
                "textures", ((CraftPlayer) player).getProfile().getProperties().get("textures"));
      }

      PlayerConnection b = ((CraftPlayer) player).getHandle().playerConnection;

      b.sendPacket(
          new PacketPlayOutPlayerInfo(
              PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity));
      PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn =
          new PacketPlayOutNamedEntitySpawn(entity);

      DataWatcher dataWatcher = entity.getDataWatcher();
      dataWatcher.watch(10, (byte) 0xFF);

      b.sendPacket(packetPlayOutNamedEntitySpawn);
      teleport(player);

      Bukkit.getScheduler()
          .runTaskLater(
              javaPlugin,
              () -> {
                PacketPlayOutPlayerInfo packetPlayOutPlayerInfo =
                    new PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity);
                PacketPlayOutPlayerInfo.PlayerInfoData data =
                    packetPlayOutPlayerInfo
                    .new PlayerInfoData(
                        gameProfile,
                        1,
                        WorldSettings.EnumGamemode.NOT_SET,
                        CraftChatMessage.fromString(entity.getProfile().getName())[0]);

                List<PacketPlayOutPlayerInfo.PlayerInfoData> players =
                    (List<PacketPlayOutPlayerInfo.PlayerInfoData>)
                            PacketEntity.getValue(packetPlayOutPlayerInfo, "b");
                players.add(data);

                PacketEntity.setValue(packetPlayOutPlayerInfo, "b", players);
                b.sendPacket(packetPlayOutPlayerInfo);
              },
              20);
    }

    public NPCImpl create() {
      MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
      WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();

      entity =
          new EntityPlayer(
              minecraftServer,
              worldServer,
              gameProfile,
              new DemoPlayerInteractManager(worldServer));
      entity.setLocation(
          location.getX(),
          location.getY(),
          location.getZ(),
          location.getYaw(),
          location.getPitch());
      return this;
    }
  }
}
