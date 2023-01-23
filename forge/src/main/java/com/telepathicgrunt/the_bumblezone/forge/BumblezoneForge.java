package com.telepathicgrunt.the_bumblezone.forge;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.events.*;
import com.telepathicgrunt.the_bumblezone.events.entity.*;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.*;
import com.telepathicgrunt.the_bumblezone.events.player.*;
import com.telepathicgrunt.the_bumblezone.mixins.forge.FireBlockInvoker;
import com.telepathicgrunt.the_bumblezone.modcompat.forge.BuzzierBeesCompatRegs;
import com.telepathicgrunt.the_bumblezone.modcompat.forge.ProductiveBeesCompatRegs;
import com.telepathicgrunt.the_bumblezone.modinit.registry.forge.ResourcefulRegistriesImpl;
import com.telepathicgrunt.the_bumblezone.modules.forge.ForgeModuleInitalizer;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.resource.PathPackResources;

import java.nio.file.Path;
import java.util.List;

@Mod(Bumblezone.MODID)
public class BumblezoneForge {

    public BumblezoneForge() {
        ForgeModuleInitalizer.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, ResourcefulRegistriesImpl::onRegisterForgeRegistries);

        Bumblezone.init();

        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        if (ModList.get().isLoaded("productivebees")) {
            ProductiveBeesCompatRegs.CONFIGURED_FEATURES.register(modEventBus);
            ProductiveBeesCompatRegs.PLACED_FEATURES.register(modEventBus);
        }

        if (ModList.get().isLoaded("buzzier_bees")) {
            BuzzierBeesCompatRegs.CONFIGURED_FEATURES.register(modEventBus);
            BuzzierBeesCompatRegs.PLACED_FEATURES.register(modEventBus);
        }

        if (FMLEnvironment.dist == Dist.CLIENT) {
            BumblezoneForgeClient.init();
        }

        modEventBus.addListener(BumblezoneForge::onRegisterPackFinder);
        modEventBus.addListener(BumblezoneForge::onRegisterAttributes);
        modEventBus.addListener(BumblezoneForge::onSetup);
        modEventBus.addListener(EventPriority.LOWEST, BumblezoneForge::onFinalSetup);
        modEventBus.addListener(BumblezoneForge::onRegisterCreativeTabs);
        modEventBus.addListener(BumblezoneForge::onAddTabContents);
        modEventBus.addListener(BumblezoneForge::onSpawnPlacements);

        eventBus.addListener(BumblezoneForge::onBabySpawn);
        eventBus.addListener(BumblezoneForge::onServerStarting);
        eventBus.addListener(BumblezoneForge::onServerStopping);
        eventBus.addListener(BumblezoneForge::onAddVillagerTrades);
        eventBus.addListener(BumblezoneForge::onWanderingTrades);
        eventBus.addListener(BumblezoneForge::onRegisterCommand);
        eventBus.addListener(BumblezoneForge::onProjectileHit);
        eventBus.addListener(EventPriority.HIGH, BumblezoneForge::onProjectileHitHigh);
        eventBus.addListener(EventPriority.LOWEST, BumblezoneForge::onBlockBreak);
        eventBus.addListener(BumblezoneForge::onPlayerTick);
        eventBus.addListener(BumblezoneForge::onPlayerRightClicked);
        eventBus.addListener(BumblezoneForge::onPickupItem);
        eventBus.addListener(BumblezoneForge::onLocateProjectile);
        eventBus.addListener(BumblezoneForge::onGrantAdvancement);
        eventBus.addListener(BumblezoneForge::onIntreactEntity);
        eventBus.addListener(BumblezoneForge::onItemCrafted);
        eventBus.addListener(BumblezoneForge::onBreakSpeed);
        eventBus.addListener(BumblezoneForge::onTagsUpdate);
        eventBus.addListener(BumblezoneForge::onLevelTick);
        eventBus.addListener(BumblezoneForge::onAddReloadListeners);
        eventBus.addListener(BumblezoneForge::onEntityAttacked);
        eventBus.addListener(BumblezoneForge::onEntityDeath);
        eventBus.addListener(BumblezoneForge::onEntitySpawn);
        eventBus.addListener(BumblezoneForge::onEntityTick);
        eventBus.addListener(BumblezoneForge::onEntityDimensionTravel);
        eventBus.addListener(BumblezoneForge::onEntityVisibility);
        eventBus.addListener(BumblezoneForge::onFinishUseItem);
        eventBus.addListener(EventPriority.LOWEST, BumblezoneForge::onEntityHurtLowest);
    }

    private static void onAddTabContents(CreativeModeTabEvent.BuildContents event) {
        AddCreativeTabEntriesEvent.EVENT.invoke(new AddCreativeTabEntriesEvent(toType(event.getTab()), event.getTab(), event::accept));
    }

    private static AddCreativeTabEntriesEvent.Type toType(CreativeModeTab tab) {
        if (CreativeModeTabs.BUILDING_BLOCKS.equals(tab)) return AddCreativeTabEntriesEvent.Type.BUILDING;
        else if (CreativeModeTabs.COLORED_BLOCKS.equals(tab)) return AddCreativeTabEntriesEvent.Type.COLORED;
        else if (CreativeModeTabs.NATURAL_BLOCKS.equals(tab)) return AddCreativeTabEntriesEvent.Type.NATURAL;
        else if (CreativeModeTabs.FUNCTIONAL_BLOCKS.equals(tab)) return AddCreativeTabEntriesEvent.Type.FUNCTIONAL;
        else if (CreativeModeTabs.REDSTONE_BLOCKS.equals(tab)) return AddCreativeTabEntriesEvent.Type.REDSTONE;
        else if (CreativeModeTabs.TOOLS_AND_UTILITIES.equals(tab)) return AddCreativeTabEntriesEvent.Type.TOOLS;
        else if (CreativeModeTabs.COMBAT.equals(tab)) return AddCreativeTabEntriesEvent.Type.COMBAT;
        else if (CreativeModeTabs.FOOD_AND_DRINKS.equals(tab)) return AddCreativeTabEntriesEvent.Type.FOOD;
        else if (CreativeModeTabs.INGREDIENTS.equals(tab)) return AddCreativeTabEntriesEvent.Type.INGREDIENTS;
        else if (CreativeModeTabs.SPAWN_EGGS.equals(tab)) return AddCreativeTabEntriesEvent.Type.SPAWN_EGGS;
        else if (CreativeModeTabs.OP_BLOCKS.equals(tab)) return AddCreativeTabEntriesEvent.Type.OPERATOR;
        return AddCreativeTabEntriesEvent.Type.CUSTOM;
    }

    private static void onRegisterCreativeTabs(CreativeModeTabEvent.Register event) {
        RegisterCreativeTabsEvent.EVENT.invoke(new RegisterCreativeTabsEvent((id, operator, initialDisplayItems) ->
                event.registerCreativeModeTab(id, builder -> {
                    operator.accept(builder);
                    builder.displayItems((flag, output, bl) -> {
                        List<ItemStack> stacks = Lists.newArrayList();
                        initialDisplayItems.accept(stacks);
                        output.acceptAll(stacks);
                    });
                })
        ));
    }

    private static void onSetup(FMLCommonSetupEvent event) {
        SetupEvent.EVENT.invoke(new SetupEvent(event::enqueueWork));

        event.enqueueWork(() ->
                RegisterFlammabilityEvent.EVENT.invoke(new RegisterFlammabilityEvent((item, igniteOdds, burnOdds) ->
                ((FireBlockInvoker) Blocks.FIRE).callSetFlammable(item, igniteOdds, burnOdds)))
        );
    }

    private static void onFinalSetup(FMLCommonSetupEvent event) {
        FinalSetupEvent.EVENT.invoke(new FinalSetupEvent(event::enqueueWork));
    }

    private static void onServerStarting(ServerStartingEvent event) {
        ServerGoingToStartEvent.EVENT.invoke(new ServerGoingToStartEvent(event.getServer()));
    }

    private static void onServerStopping(ServerStoppingEvent event) {
        ServerGoingToStopEvent.EVENT.invoke(ServerGoingToStopEvent.INSTANCE);
    }

    private static void onRegisterPackFinder(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            AddBuiltinResourcePacks.EVENT.invoke(new AddBuiltinResourcePacks((id, displayName, mode) -> {
                Path resourcePath = ModList.get().getModFileById(id.getNamespace()).getFile().findResource("resourcepacks/" + id.getPath());

                final Pack.Info info = createInfoForLatest(displayName, mode == AddBuiltinResourcePacks.PackMode.FORCE_ENABLED);
                final Pack pack = Pack.create(
                    "builtin/add_pack_finders_test", displayName,
                    mode == AddBuiltinResourcePacks.PackMode.FORCE_ENABLED,
                    (path) -> new PathPackResources(path, true, resourcePath),
                    info, PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, false, createSource(mode)
                );
                event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
            }));
        }
    }

    private static Pack.Info createInfoForLatest(Component description, boolean hidden) {
        return new Pack.Info(
                description,
                SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA.bridgeType),
                SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES.bridgeType),
                FeatureFlagSet.of(),
                hidden
        );
    }

    private static PackSource createSource(AddBuiltinResourcePacks.PackMode mode) {
        final Component text = Component.translatable("pack.source.builtin");
        return PackSource.create(
                component -> Component.translatable("pack.nameAndSource", component, text).withStyle(ChatFormatting.GRAY),
                mode != AddBuiltinResourcePacks.PackMode.USER_CONTROLLED
        );
    }

    private static void onBabySpawn(BabyEntitySpawnEvent event) {
        boolean cancel = BabySpawnEvent.EVENT.invoke(new BabySpawnEvent(event.getParentA(), event.getParentB(), event.getCausedByPlayer(), event.getChild()), event.isCanceled());
        if (cancel) {
            event.setCanceled(true);
        }
    }

    private static void onRegisterAttributes(EntityAttributeCreationEvent event) {
        RegisterEntityAttributesEvent.EVENT.invoke(new RegisterEntityAttributesEvent((entity, builder) -> event.put(entity, builder.build())));
    }

    private static void onAddVillagerTrades(VillagerTradesEvent event) {
        RegisterVillagerTradesEvent.EVENT.invoke(new RegisterVillagerTradesEvent(event.getType(), (i, listing) -> event.getTrades().get(i.intValue()).add(listing)));
    }

    private static void onWanderingTrades(WandererTradesEvent event) {
        RegisterWanderingTradesEvent.EVENT.invoke(new RegisterWanderingTradesEvent(event.getGenericTrades()::add, event.getRareTrades()::add));
    }

    private static void onRegisterCommand(net.minecraftforge.event.RegisterCommandsEvent event) {
        RegisterCommandsEvent.EVENT.invoke(new RegisterCommandsEvent(event.getDispatcher(), event.getCommandSelection(), event.getBuildContext()));
    }

    private static void onProjectileHit(ProjectileImpactEvent event) {
        boolean cancel = ProjectileHitEvent.EVENT.invoke(new ProjectileHitEvent(event.getProjectile(), event.getRayTraceResult()), event.isCanceled());
        if (cancel) {
            event.setCanceled(true);
        }
    }

    private static void onProjectileHitHigh(ProjectileImpactEvent event) {
        boolean cancel = ProjectileHitEvent.EVENT_HIGH.invoke(new ProjectileHitEvent(event.getProjectile(), event.getRayTraceResult()), event.isCanceled());
        if (cancel) {
            event.setCanceled(true);
        }
    }

    private static void onBlockBreak(BlockEvent.BreakEvent event) {
        boolean cancel = BlockBreakEvent.EVENT_LOWEST.invoke(new BlockBreakEvent(event.getPlayer(), event.getState()), event.isCanceled());
        if (cancel) {
            event.setCanceled(true);
        }
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerTickEvent.EVENT.invoke(new PlayerTickEvent(event.player, event.phase == TickEvent.Phase.END));
    }

    private static void onPlayerRightClicked(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = PlayerRightClickedBlockEvent.EVENT.invoke(new PlayerRightClickedBlockEvent(event.getEntity(), event.getHand(), event.getPos(), event.getHitVec()));
        if (result != null) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void onPickupItem(PlayerEvent.ItemPickupEvent event) {
        PlayerPickupItemEvent.EVENT.invoke(new PlayerPickupItemEvent(event.getEntity(), event.getStack()));
    }

    private static void onLocateProjectile(LivingGetProjectileEvent event) {
         ItemStack stack = PlayerLocateProjectileEvent.EVENT.invoke(new PlayerLocateProjectileEvent(event.getProjectileWeaponItemStack(), event.getEntity()), event.getProjectileItemStack());
         if (stack != null) {
             event.setProjectileItemStack(stack);
         }
    }

    private static void onGrantAdvancement(AdvancementEvent event) {
        PlayerGrantAdvancementEvent.EVENT.invoke(new PlayerGrantAdvancementEvent(event.getAdvancement(), event.getEntity()));
    }

    private static void onIntreactEntity(PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = PlayerEntityInteractEvent.EVENT.invoke(new PlayerEntityInteractEvent(event.getEntity(), event.getTarget(), event.getHand()));
        if (result != null) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        PlayerCraftedItemEvent.EVENT.invoke(new PlayerCraftedItemEvent(event.getEntity(), event.getCrafting(), event.getInventory()));
    }

    private static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        AtomicDouble speed = new AtomicDouble(event.getNewSpeed());
        PlayerBreakSpeedEvent.EVENT.invoke(new PlayerBreakSpeedEvent(event.getEntity(), event.getState(), speed));
        event.setNewSpeed(speed.floatValue());
    }

    private static void onTagsUpdate(net.minecraftforge.event.TagsUpdatedEvent event) {
        TagsUpdatedEvent.EVENT.invoke(new TagsUpdatedEvent(event.getRegistryAccess(), event.getUpdateCause() == net.minecraftforge.event.TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED));
    }

    private static void onSpawnPlacements(SpawnPlacementRegisterEvent event) {
        RegisterSpawnPlacementsEvent.EVENT.invoke(new RegisterSpawnPlacementsEvent(BumblezoneForge.registerPlacement(event)));
    }

    private static RegisterSpawnPlacementsEvent.Registrar registerPlacement(SpawnPlacementRegisterEvent event) {
        return new RegisterSpawnPlacementsEvent.Registrar() {
            @Override
            public <T extends Mob> void register(EntityType<T> type, RegisterSpawnPlacementsEvent.Placement<T> place) {
                event.register(type, place.spawn(), place.height(), place.predicate(), SpawnPlacementRegisterEvent.Operation.AND);
            }
        };
    }

    private static void onLevelTick(TickEvent.LevelTickEvent event) {
        LevelTickEvent.EVENT.invoke(new LevelTickEvent(event.level, event.phase == TickEvent.Phase.END));
    }

    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        RegisterReloadListenerEvent.EVENT.invoke(new RegisterReloadListenerEvent((id, listener) -> event.addListener(listener)));
    }

    private static void onFinishUseItem(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = FinishUseItemEvent.EVENT.invoke(new FinishUseItemEvent(event.getEntity(), event.getItem(), event.getDuration()));
        if (stack != null) {
            event.setResultStack(stack);
        }
    }

    private static void onEntityVisibility(LivingEvent.LivingVisibilityEvent event) {
        EntityVisibilityEvent visibilityEvent = new EntityVisibilityEvent(event.getVisibilityModifier(), event.getEntity(), event.getLookingEntity());
        EntityVisibilityEvent.EVENT.invoke(visibilityEvent);
        event.modifyVisibility(visibilityEvent.visibility() / event.getVisibilityModifier());
    }

    private static void onEntityDimensionTravel(EntityTravelToDimensionEvent event) {
        boolean cancel = EntityTravelingToDimensionEvent.EVENT.invoke(new EntityTravelingToDimensionEvent(event.getDimension(), event.getEntity()), event.isCanceled());
        if (cancel) {
            event.setCanceled(true);
        }
    }

    private static void onEntityTick(LivingEvent.LivingTickEvent event) {
        EntityTickEvent.EVENT.invoke(new EntityTickEvent(event.getEntity()));
    }

    private static void onEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
        boolean cancel = EntitySpawnEvent.EVENT.invoke(new EntitySpawnEvent(event.getEntity(), event.getLevel(), event.getEntity().isBaby(), event.getSpawnReason()), event.isCanceled());
        if (cancel) {
            event.setCanceled(true);
        }
    }

    private static void onEntityHurtLowest(LivingHurtEvent event) {
        boolean cancel = EntityHurtEvent.EVENT_LOWEST.invoke(new EntityHurtEvent(event.getEntity(), event.getSource(), event.getAmount()), event.isCanceled());
        if (cancel) {
            event.setCanceled(true);
        }
    }

    private static void onEntityDeath(LivingDeathEvent event) {
        boolean cancel = EntityDeathEvent.EVENT.invoke(new EntityDeathEvent(event.getEntity(), event.getSource()), event.isCanceled());
        if (cancel) {
            event.setCanceled(true);
        }
    }

    private static void onEntityAttacked(LivingAttackEvent event) {
        boolean cancel = EntityAttackedEvent.EVENT.invoke(new EntityAttackedEvent(event.getEntity(), event.getSource(), event.getAmount()), event.isCanceled());
        if (cancel) {
            event.setCanceled(true);
        }
    }

}
