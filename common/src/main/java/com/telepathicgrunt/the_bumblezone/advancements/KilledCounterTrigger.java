package com.telepathicgrunt.the_bumblezone.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telepathicgrunt.the_bumblezone.items.BeeArmor;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;

public class KilledCounterTrigger extends SimpleCriterionTrigger<KilledCounterTrigger.Instance> {
    private final ResourceLocation id;

    public KilledCounterTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public KilledCounterTrigger.Instance createInstance(JsonObject jsonObject, ContextAwarePredicate predicate, DeserializationContext deserializationContext) {
        JsonElement beeArmorJson = jsonObject.get("bee_armor_required");
        JsonElement targetTagJson = jsonObject.get("is_target_tag");
        return new KilledCounterTrigger.Instance(
                predicate,
                new ResourceLocation(jsonObject.get("target_entity").getAsString()),
                jsonObject.get("target_count").getAsInt(),
                targetTagJson != null && targetTagJson.getAsBoolean(),
                beeArmorJson != null && beeArmorJson.getAsBoolean());
    }

    public void trigger(ServerPlayer serverPlayer, Entity currentEntity, int currentCount) {
        super.trigger(serverPlayer, (trigger) -> trigger.matches(serverPlayer, currentEntity, currentCount));
    }

    public class Instance extends AbstractCriterionTriggerInstance {
        private final int targetCount;
        private final ResourceLocation targetEntity;
        private final boolean isTargetTag;
        private final boolean beeArmorRequired;

        public Instance(ContextAwarePredicate predicate, ResourceLocation targetEntity, int targetCount, boolean isTargetTag, boolean beeArmorRequired) {
            super(id, predicate);
            this.targetCount = targetCount;
            this.targetEntity = targetEntity;
            this.isTargetTag = isTargetTag;
            this.beeArmorRequired = beeArmorRequired;
        }

        public boolean matches(ServerPlayer serverPlayer, Entity currentEntity, int currentCount) {
            boolean entityMatch;
            if (this.isTargetTag) {
                entityMatch = currentEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, this.targetEntity));
            }
            else {
                entityMatch = BuiltInRegistries.ENTITY_TYPE.getKey(currentEntity.getType()).equals(this.targetEntity);
            }

            return entityMatch &&
                    (!this.beeArmorRequired || BeeArmor.getBeeThemedGearCount(serverPlayer) >= 4) &&
                    currentCount >= this.targetCount;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonobject = super.serializeToJson(serializationContext);
            jsonobject.addProperty("target_count", this.targetCount);
            jsonobject.addProperty("target_entity", this.targetEntity.toString());
            jsonobject.addProperty("is_target_tag", this.isTargetTag);
            jsonobject.addProperty("bee_armor_required", this.beeArmorRequired);
            return jsonobject;
        }
    }
}
