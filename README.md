# PacketHelper
First you need a instance of `PacketEntityManager.class`.
Create a new instance using `PacketEntityManager.create(JavaPlugin);`.
You have to register each NPC or hologram in the manager, you do that with `PacketEntityManager#register()`.

## Installation
You have to compile the API into the finished plugin. ProtocolLib is required.

## Implementations

### NPC (PacketEntity, Lookable, Clickable, Equipable)
How to create a NPC?
`NPC.create(Location location, boolean looking, Function<Player, String> nameFunction, JavaPlugin javaPlugin)`

#### NameFunction?
NameFunction sets the name of the npc, which can be different from player to player.

### Hologram (PacketEntity)
How to create a Hologram?
`Hologram.create(Function<Player, String> nameFunction, Location location)`

#### NameFunction?
NameFunction sets the text of the hologram, which can be different from player to player.

### RotatingHead (PacketEntity, AutoRotatable, Equipable)
You can create rotating heads: `new RotatingHead(String)`

### PacketAnimal (PacketEntity, Clickable, Lookable, Equipable)
You can create any Entity using packets witch inherits from `EntityInsentient.java`

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
Syncronize the location of the server with that of the player: `Teleportable#teleport(Player player)` 