# PacketHelper
First you need a instance of `PacketEntityManager.class`.
Create a new instance using `PacketEntityManager.create(JavaPlugin);`.
You have to register each NPC or hologram in the manager, you do that with `PacketEntityManager#register()`.

## Installation
You have to compile the API into the finished plugin. ProtocolLib is required.

## Implementations

### dev.buchstabet.packethelper.implementation.NPC (PacketEntity, Lookable, Clickable, Equipable)
How to create a NPC?
`NPC.create(Location location, boolean looking, Function<Player, String> nameFunction, JavaPlugin javaPlugin)`

#### NameFunction?
NameFunction sets the name of the npc, which can be different from player to player.

### dev.buchstabet.packethelper.implementation.Hologram (PacketEntity)
How to create a Hologram?
`Hologram.create(Function<Player, String> nameFunction, Location location)`

#### NameFunction?
NameFunction sets the text of the hologram, which can be different from player to player.

### dev.buchstabet.packethelper.implementation.RotatingHead (PacketEntity, AutoRotatable, Equipable)
You can create rotating heads: `new RotatingHead(String)`

### dev.buchstabet.packethelper.implementation.PacketAnimal (PacketEntity, Clickable, Lookable, Equipable)
You can create any Entity using packets witch inherits from `EntityInsentient.java`

## Properties (Inheritances)

### dev.buchstabet.packethelper.AutoRotatable
Entities are rotated automatically

### dev.buchstabet.packethelper.Clickable
You can register a ClickEvent: `Clickable#registerClickEvent(BiConsumer<Player, PacketContainer> consumer, JavaPlugin javaPlugin)`

### dev.buchstabet.packethelper.Equipable
You can change the equipment of an entity: `Equipable#equip(Player player, int slot, ItemStack itemStack)`

### dev.buchstabet.packethelper.Lookable
Entities inheriting from this type look after the player.

### dev.buchstabet.packethelper.PacketEntity
Each entity to be handled by PacketHelper must inherit from PacketEntity.

### dev.buchstabet.packethelper.Rotatable (Teleportable)
Entities with this type, can be rotated: `Rotatable#rotate(Player player, float yaw)`

### dev.buchstabet.packethelper.Teleportable
Change the location of an entity: `Teleportable#teleport(Player player)` 
But do not forget to set the location: `Teleportable#setLocation(Location location)`
