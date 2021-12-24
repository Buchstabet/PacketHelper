# PacketHelper
First you need a instance of `PacketEntityManager.class`.
Create a new instance using `PacketEntityManager.create(JavaPlugin);`.
You have to register each NPC or hologram in the manager, you do that with `PacketEntityManager#register()`.

## Installation
You have to compile the API into the finished plugin. ProtocolLib is required.

## Implementations
Call the constructor you want and register it in your `PacketEntityManager#register(PacketEntity)`
- NPC (PacketEntity, Lookable, Clickable, Equipable)
- Hologram (PacketEntity)
- FlyingItem (PacketEntity, Teleportable, Equipable)
- RotatingHead (FlyingItem)
- PacketAnimal (PacketEntity, Clickable, Lookable, Equipable)

## Properties (Inheritances)

### AutoRotatable
Entities are rotated automatically

### Clickable
You can register a ClickEvent: `Clickable#registerClickEvent(BiConsumer<Player, PacketContainer> consumer, JavaPlugin javaPlugin)`

### Equipable
You can change the equipment of an entity: `Equipable#equip(Player player, int slot, ItemStack itemStack)`

### Lookable
Entities inheriting from this type look after the player.

### PacketEntity
Each entity to be handled by PacketHelper must inherit from PacketEntity.

### Rotatable (Teleportable)
Entities with this type, can be rotated: `Rotatable#rotate(Player player, float yaw)`

### Teleportable
Change the location of an entity: `Teleportable#teleport(Player player)` 
But do not forget to set the location: `Teleportable#setLocation(Location location)`
