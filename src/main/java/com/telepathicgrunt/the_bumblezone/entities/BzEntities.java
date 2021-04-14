package com.telepathicgrunt.the_bumblezone.entities;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.entities.mobs.HoneySlimeEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BzEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Bumblezone.MODID);

    public static final EntityType<HoneySlimeEntity> HONEY_SLIME_RAW = EntityType.Builder.<HoneySlimeEntity>create(HoneySlimeEntity::new, EntityClassification.CREATURE).size(0.6F, 1.99F).trackingRange(8).build("honey_slime");
    public static final RegistryObject<EntityType<HoneySlimeEntity>> HONEY_SLIME = ENTITIES.register("honey_slime", () -> HONEY_SLIME_RAW);

    public static void registerAdditionalEntityInformation() {
        registerEntitySpawnRestrictions();
    }

    private static void registerEntitySpawnRestrictions() {
        EntitySpawnPlacementRegistry.register(HONEY_SLIME.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canSpawnOn);
    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(HONEY_SLIME.get(), HoneySlimeEntity.getAttributeBuilder().create());
    }
}
