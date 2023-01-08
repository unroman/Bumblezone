### **(V.6.6.1 Changes) (1.19.3 Minecraft)**

##### Blocks:
Fixed Honeycomb Brood Blocks able to spawn bees in fluids.

##### Misc:
Fixed random potential server crash.


### **(V.6.6.0 Changes) (1.19.3 Minecraft)**

##### Structures:
Made Throne Pillar locating Honey Compasses more common in Cell Maze structure.

Added Bee House, Candle Parkour, Battle Cubes, Stinger Spear Shrine, Ice Monolith, Overground Flower,
Pirate Ship, Dance Floor, Cannon Range, Honitel, and Honey Fountain structures!

Honitel and Battle Cube structures has a chance of having a Honey Compass that can locate the Throne Pillar structure!

Fixed the giant honeycomb hole feature from eating away at many structures.

Made Honey Cave Room structure much more common so it is more easily found.
 Also improve the bottom of the structure to not be as flat. (Also fixed their Honey Cocoon not having loot)

Disabled prefilling maps in Hive Temple's loot due to unable to resolve the lag with it.

Fixed Throne Pillar structure's giant webs not turning into Filled Porous Honeycomb Block when replacing Sugar Water.

##### Items:
Fixed Stinger Spear losing enchantments and damage if thrown, game closed, reopened, and the spear picked up again.

Fixed Crystal Cannon firing center crystal shard backwards.

Bee armor now prevents freezing effect that Powder Snow gives. tagged as `minecraft:freeze_immune_wearables`. Bundle up for the cold!

Honey Compass is now tagged in `minecraft:compasses` tag. Let me know if this ends up being an issue.

Fixed Stingless Bee Helmet not highlighting Bee Queen mob.

Fixed Stingless Bee Helmet still highlighting bees even after taking it off.
 Also highlights if shift clicking while on ground now instead of any shift clicking.

Fixed Honey Crystal Shield able to get Mending enchantment.

Fixed Stinger Spear able to get Channeling and Riptide enchantments.

##### Blocks:
Added String Curtains that bees cannot pathfind through and slightly pushes bees away. Great for making a walk-in bee room with bees escaping!
 Attaches to any solid surface and right clicking with string will extend the block downward.
 Dispensers with string can extend the curtain and Comparators counts how far down the curtain goes under the checked spot (up to 15 power) 
 Has vibration dampening that blocks sculk sensor from hearing a sound on the other side of the curtain.
 Recipe to create the curtain is this with (T) Stick, (S) String, (G) Glass, and (D) a Dye which is optional:
 T T T
 S G S
 S D S

Some tags that the String Curtains uses are:
 `the_bumblezone:string_curtains` (item tag)

 `the_bumblezone:string_curtains` (block tag)

 `the_bumblezone:string_curtains/curtain_extending` (item tag)

 `the_bumblezone:string_curtains/blocks_pathfinding_for_non_bee_mob` (entity tag)

 `the_bumblezone:string_curtains/force_allow_pathfinding` (entity tag)

Empty Porous Honeycomb Block will transform into a Filled Porous Honeycomb Block when touched by a pollinated bee!

Empty Honeycomb Brood Block will transform into a Honeycomb Brood Block with a larva inside when touched by a pollinated bee!

Crystalline Flower, Honey Crystal, and Glistering Honey Crystal now has crystal sounds when placed, broken, stepped on, or shot at.

Fixed Super Candles/Incense Candles able to deal damage to fire immune mobs in its flame.

Added superCandlesBurnsMobs config option to allow disabling Super Candles/Incense Candles's flame from setting mobs on fire.

Crystalline Flower screen refactored a bit to now send the enchantments to show from the server to the client.
 This should help against any enchantment registry desync issue between server and client such as
 another mod not registering an enchantment on the server.

Crystalline Flower now gains 2xp instead of 1xp when consuming diamond, emerald, quartz, echo shard, and amethyst shard.
 Amethyst Block, Quartz Block, Diamond Block, and Emerald Block gives 5xp.

Crystalline Flower now shows the name of the mod that the enchantment is from in the tooltip.
 Switch on advanced tooltip settings (F3 + H) to see full resourcelocation of enchantment instead.
 Also, will attempt to auto-translate enchantments that have no translation by using the path
 of the enchantment's resourcelocation and formatting it to be cleaner.

##### Fluids:
Fixed issue where mobs would not see Sugar Water, Honey Fluid, or Royal Jelly Fluid as water when pathfinding.
 Bees should now properly avoid flying into these liquids and suffocating. (Hopefully...)

Non-source Honey Fluid cannot be transformed into source blocks anymore by pollinated bees.
 Stops bees from messing up structures or builds as much as they currently are.

##### Entities:
Throwing/dropping items from player to Bee Queen will now trigger/progress the Bee Queen trade advancements for that player.
 This means dropping 64 stacks of an item is a superfast way to progress the 256 Bee Queen trade advancement.

Fixed some typos in translation keys for Bee Queen saying `beehemoth_queen` instead of `bee_queen`.

Tagged Honey Slime as `minecraft:frog_food` so Frogs can eat them now.

Bee Queen now has a super trade system! Every 20 minutes, the Bee Queen will ask players very close by to trade with it up to 24 time
 with a specific item for triple the reward! Has 3 config settings that setting any of the configs to 0 will disable this system.

Added some more Bee Queen trade possibilities. Try trading Sweet Berries, Glow Berries, Cocoa Beans, Bricks, Clay Ball,
 Snowball, Magma Cream, Iron Ingot, Gold Ingot, Copper Ingot, Nether Brick, Mushroom Stew, Beetroot Soup, or Potion of Bees's potions!

##### Effects:
Hopefully remove Wrath of the Hive from more bees properly when the target dies.

##### Biomes:
Fixed biome spawning that was spawning blobs of biomes that shouldn't be blobs.

Renamed nonstandard_biomes field in dimension json to blob_biomes where it takes a biome tag of biomes to spawn as a 
 blob of area. Crystal Canyon and Hive Pillar are added to this by default and can spawn over a medium sized area as a blob.

##### Commands:
`bumblezone` command was renamed to `bumblezone_read_self_data` command for anyone to use to check their own data.

`bumblezone_modify_data` and `bumblezone_read_data` commands were added to allow for op players to check data of 
 any players or change the essence status of any player. Permission level 2 required by the command user.
 Example usage that removes essence from someone: `/bumblezone_modify_data is_bee_essenced SomePersonOnServer false`

##### Misc:
Fixed bug where I was checking for crouching pose instead of if player was holding down shift key. (isCrouching vs isShiftKeyDown)
 Should make it so stuff like Carpenter Bees Boots do not mine automatically if you are in a 1.5 block high space.
 It will only mine if you specifically are holding down crouching key. Same with helmet and leggings behaviors.

Silence "Hanging entity at invalid position" logspam from vanilla by lowering the logging level of that event from error to debug level.
 Mojang bug report: https://bugs.mojang.com/browse/MC-252934
 The Item Frame spawned from nbt files still places as intended and functions as intended.
 This was just annoying logspam I decided to yeet.

Made Bumblezone's off thread tasks properly log any crash into the log file if they die.

##### Recipes:
Organized Bumblezone recipe json files much better into sub folders. Cleaner and easier to browse.

##### Tags:
Added the following new tags:

`the_bumblezone:dimension_teleportation/forced_allowed_teleportable_blocks` (block tag)

`the_bumblezone:honey_compass/forced_allowed_position_tracking` (block tag)

`the_bumblezone:stingless_bee_helmet/forced_allowed_passengers` (entity tag)

Also added is `the_bumblezone:crystalline_flower/forced_allowed_enchantments` enchantment tag for Crystalline Flower. 
 This will force an enchantment to always show regardless of the isDiscoverable value in the enchantment itself. 
 It will also show the enchantment at max tier even if the  enchantment requires more enchanting power than 
 the flower can provide. Also overrides the disallow tag.

Organized Bumblezone tags much better into sub folders. Changes are:

`the_bumblezone:blacklisted_crystalline_flower_enchantments` -> `the_bumblezone:crystalline_flower/disallowed_enchantments` (enchantment tag

`the_bumblezone:blacklisted_incense_candle_effects` -> `the_bumblezone:incense_candle/disallowed_effects` (mob effect tag)

`the_bumblezone:allowed_hanging_garden_flowers` -> `the_bumblezone:hanging_garden/allowed_flowers` (block tag)

`the_bumblezone:allowed_hanging_garden_tall_flowers` -> `the_bumblezone:hanging_garden/allowed_tall_flowers` (block tag)

`the_bumblezone:allowed_hanging_garden_leaves` -> `the_bumblezone:hanging_garden/allowed_leaves` (block tag)

`the_bumblezone:allowed_hanging_garden_logs` -> `the_bumblezone:hanging_garden/allowed_logs` (block tag)

`the_bumblezone:blacklisted_hanging_garden_flowers` -> `the_bumblezone:hanging_garden/forced_disallowed_flowers` (block tag)

`the_bumblezone:blacklisted_hanging_garden_tall_flowers` -> `the_bumblezone:hanging_garden/forced_disallowed_tall_flowers` (block tag)

`the_bumblezone:blacklisted_hanging_garden_leaves` -> `the_bumblezone:hanging_garden/forced_disallowed_leaves` (block tag)

`the_bumblezone:blacklisted_hanging_garden_logs` -> `the_bumblezone:hanging_garden/forced_disallowed_logs` (block tag)

`the_bumblezone:flowers_allowed_by_pollen_puff` -> `the_bumblezone:pollen_puff/multiplying_allowed_flowers` (block tag)

`the_bumblezone:flowers_blacklisted_from_pollen_puff` -> `the_bumblezone:pollen_puff/multiplying_forced_disallowed_flowers` (block tag)

`the_bumblezone:carpenter_bee_boots_climbables` -> `the_bumblezone:carpenter_bee_boots/climbables` (block tag)

`the_bumblezone:carpenter_bee_boots_mineables` -> `the_bumblezone:carpenter_bee_boots/mineables` (block tag)

`the_bumblezone:blacklisted_teleportable_hive_blocks` -> `the_bumblezone:dimension_teleportation/disallowed_teleportable_beehive_blocks` (block tag)

`the_bumblezone:required_blocks_under_hive_to_teleport` -> `the_bumblezone:dimension_teleportation/required_blocks_under_beehive_to_teleport` (block tag)

`the_bumblezone:cave_edge_blocks_for_modded_compats` -> `the_bumblezone:worldgen_checks/cave_edge_blocks_for_modded_compats` (block tag)

`the_bumblezone:honeycombs_that_features_can_carve` -> `the_bumblezone:worldgen_checks/honeycombs_that_features_can_carve` (block tag)

`the_bumblezone:wrath_activating_blocks_when_mined` -> `the_bumblezone:bee_aggression_in_dimension/wrath_activating_blocks_when_mined` (block tag)

`the_bumblezone:blacklisted_honey_compass_blocks` -> `the_bumblezone:honey_compass/beehives_disallowed_from_position_tracking` (block tag)

`the_bumblezone:blacklisted_bee_cannon_bees` -> `the_bumblezone:bee_cannon/disallowed_bee_pickup` (entity type tag)

`the_bumblezone:hanging_gardens_initial_spawn_entities` -> `the_bumblezone:hanging_garden/initial_spawn_entities` (entity type tag)

`the_bumblezone:pollen_puff_can_pollinate` -> `the_bumblezone:pollen_puff/can_pollinate` (entity type tag)

`the_bumblezone:blacklisted_stingless_bee_helmet_passengers` -> `the_bumblezone:stingless_bee_helmet/disallowed_passengers` (entity type tag)

`the_bumblezone:bee_armor_repair_items` -> `the_bumblezone:repair_items/bee_armor` (item tag)

`the_bumblezone:bee_cannon_repair_items` -> `the_bumblezone:repair_items/bee_cannon` (item tag)

`the_bumblezone:crystal_cannon_repair_items` -> `the_bumblezone:repair_items/crystal_cannon` (item tag)

`the_bumblezone:honey_crystal_shield_repair_items` -> `the_bumblezone:repair_items/honey_crystal_shield` (item tag)

`the_bumblezone:stinger_spear_items` -> `the_bumblezone:repair_items/stinger_spear` (item tag)

`the_bumblezone:consumable_candle_lighting_items` -> `the_bumblezone:candle_lightables/consumable` (item tag)

`the_bumblezone:damagable_candle_lighting_items` -> `the_bumblezone:candle_lightables/damageable` (item tag)

`the_bumblezone:infinite_candle_lighting_items` -> `the_bumblezone:candle_lightables/infinite` (item tag)

`the_bumblezone:wrath_activating_items_when_picked_up` -> `the_bumblezone:bee_aggression_in_dimension/wrath_activating_items_when_picked_up` (item tag)

`the_bumblezone:bee_feeding_items` -> `the_bumblezone:bee_feedable_items` (item tag)

`the_bumblezone:honey_compass_default_locating` -> `the_bumblezone:honey_compass/default_locating` (structure tag)

`the_bumblezone:honey_compass_from_hive_temple` -> `the_bumblezone:honey_compass/misc_locating` (structure tag)

`the_bumblezone:honey_compass_throne_locating` -> `the_bumblezone:honey_compass/throne_locating` (structure tag)

##### Mod Compat:

Setup compat with Friends and Foes's new Moobloom mob types from their Flowery Mooblooms mod.

