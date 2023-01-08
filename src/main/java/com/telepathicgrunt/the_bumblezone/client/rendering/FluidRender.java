package com.telepathicgrunt.the_bumblezone.client.rendering;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;

import java.util.function.Function;

@ClientOnly
public class FluidRender {
    public static void setupFluidRendering(final Fluid still, final Fluid flowing, final ResourceLocation stillTextureFluidId, final ResourceLocation flowTextureFluidId, boolean waterColored) {
        final ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(still);
        final ResourceLocation listenerId = new ResourceLocation(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

        final TextureAtlasSprite[] fluidSprites = { null, null };

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(new SimpleSynchronousResourceReloader() {
            @Override
            public ResourceLocation getQuiltId() {
                return listenerId;
            }

            /**
             * Get the sprites from the block atlas when resources are reloaded
             */
            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
                fluidSprites[0] = atlas.apply(stillTextureFluidId);
                fluidSprites[1] = atlas.apply(flowTextureFluidId);
            }
        });

        // The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering
        final FluidRenderHandler renderHandler = new FluidRenderHandler() {
            @Override
            public TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter view, BlockPos pos, FluidState state) {
                return fluidSprites;
            }

            @Override
            public int getFluidColor(BlockAndTintGetter view, BlockPos pos, FluidState state) {
                return waterColored && view != null && pos != null ? BiomeColors.getAverageWaterColor(view, pos) : -1;
            }
        };

        FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
    }
}
