package dev.buchstabet.packethelper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface NPC extends Lookable<EntityPlayer> {

  static NPC create(
      Location location,
      boolean looking,
      Function<Player, String> nameFunction,
      JavaPlugin javaPlugin) {
    return new NPCImpl(location, looking, nameFunction, javaPlugin);
  }

  static NPC create(
      Location location, Function<Player, String> nameFunction, JavaPlugin javaPlugin) {
    return new NPCImpl(location, nameFunction, javaPlugin);
  }

  @Getter
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  class NPCImpl extends ArrayList<UUID> implements NPC {
    private final Location location;
    private final Property skinData = null;
    private GameProfile gameProfile;
    private final String name = "BOB";
    private EntityPlayer entity;
    @Setter private boolean looking = false;
    private final Function<Player, String> nameFunction;
    private final JavaPlugin javaPlugin;

    private NPCImpl(
        Location location,
        boolean looking,
        Function<Player, String> nameFunction,
        JavaPlugin javaPlugin) {
      this.location = location;
      this.looking = looking;
      this.nameFunction = nameFunction;
      this.javaPlugin = javaPlugin;
    }

    public NPCImpl setClickEvent(
        JavaPlugin javaPlugin, BiConsumer<Player, PacketContainer> consumer) {
      ProtocolLibrary.getProtocolManager()
          .addPacketListener(
              new PacketAdapter(javaPlugin, PacketType.Play.Client.USE_ENTITY) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                  int entityId = (int) event.getPacket().getModifier().getValues().get(0);
                  if (entityId != entity.getId()) {
                    return;
                  }

                  consumer.accept(event.getPlayer(), event.getPacket());
                }
              });
      return this;
    }

    public NPCImpl buildGameProfile() {
      gameProfile = new GameProfile(UUID.randomUUID(), name);
      if (skinData != null) {
        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", skinData);
      }
      return this;
    }

    @SneakyThrows
    public void spawn(Player player) {
      Field field = GameProfile.class.getDeclaredField("name");
      field.setAccessible(true);
      field.set(gameProfile, nameFunction.apply(player));
      field.setAccessible(false);

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
      b.sendPacket(
          new PacketPlayOutEntityHeadRotation(entity, (byte) (location.getYaw() * 256 / 360)));

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
                        getValue(packetPlayOutPlayerInfo, "b");
                players.add(data);
                setValue(packetPlayOutPlayerInfo, "b", players);

                b.sendPacket(packetPlayOutPlayerInfo);
              },
              20);
    }

    public void destroy(Player player) {
      PlayerConnection b = ((CraftPlayer) player).getHandle().playerConnection;
      b.sendPacket(new PacketPlayOutEntityDestroy(entity.getId()));
    }

    private void setValue(Object obj, String name, Object value) {
      try {
        Field field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj, value);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private Object getValue(Object obj, String name) {
      try {
        Field field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(obj);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    public void look(Player player) {
      Location location = this.location.clone();
      location.setDirection(player.getLocation().toVector().subtract(location.toVector()));

      PlayerConnection b = ((CraftPlayer) player).getHandle().playerConnection;
      b.sendPacket(
          new PacketPlayOutEntityHeadRotation(entity, (byte) (location.getYaw() * 256 / 360)));
      entity.setLocation(
          location.getX(),
          location.getY(),
          location.getZ(),
          location.getYaw(),
          location.getPitch());
      b.sendPacket(new PacketPlayOutEntityTeleport(entity));
    }

    public void equip(Player player, int slot, org.bukkit.inventory.ItemStack itemStack) {
      PacketPlayOutEntityEquipment packetPlayOutEntityEquipment =
          new PacketPlayOutEntityEquipment(
              entity.getId(), slot, CraftItemStack.asNMSCopy(itemStack));
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment);
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