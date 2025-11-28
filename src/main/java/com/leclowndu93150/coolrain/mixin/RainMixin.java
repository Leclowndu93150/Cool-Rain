package com.leclowndu93150.coolrain.mixin;

import com.leclowndu93150.coolrain.CoolRainSounds;
import com.leclowndu93150.coolrain.tags.ModTags;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

@Mixin(LevelRenderer.class)
public abstract class RainMixin {

    @Shadow
    private int rainSoundTime;

    @Shadow
    private Minecraft minecraft;

    @Inject(
            method = "tickRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void onRainSoundAbove(Camera camera, CallbackInfo ci, @Local(ordinal = 1) BlockPos soundPos) {
        handleCustomRainSound(camera, soundPos, true);
        ci.cancel();
    }

    @Inject(
            method = "tickRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void onRainSoundNormal(Camera camera, CallbackInfo ci, @Local(ordinal = 1) BlockPos soundPos) {
        handleCustomRainSound(camera, soundPos, false);
        ci.cancel();
    }

    @Unique
    private void handleCustomRainSound(Camera camera, BlockPos soundPos, boolean isMuffled) {
        ClientLevel world = this.minecraft.level;
        if (world == null || soundPos == null) return;

        RandomSource random = world.random;
        BlockState blockState = world.getBlockState(soundPos);

        playRainSound(world, soundPos, blockState, random, isMuffled);
    }

    @Unique
    private void playRainSound(ClientLevel world, BlockPos pos, BlockState blockState, RandomSource random, boolean isMuffled) {
        SoundEvent sound = getRainSoundForBlock(blockState);
        float volume = getRainSoundVolume(blockState, isMuffled);
        float pitch = getRainSoundPitch(blockState, random);
        boolean shouldAttenuate = shouldAttenuateSound(blockState);

        if (sound == CoolRainSounds.RAIN_SOUNDS_AMETHYST.get() || sound == CoolRainSounds.RAIN_SOUNDS_NOTEBLOCK.get()) {
            world.playLocalSound(pos, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
        }

        if (sound == CoolRainSounds.RAIN_SOUNDS_LAVA.get()) {
            world.playLocalSound(pos, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.1F, pitch, false);
        }

        world.playLocalSound(pos, sound, SoundSource.WEATHER, volume, pitch, shouldAttenuate);
    }

    @Unique
    private SoundEvent getRainSoundForBlock(BlockState blockState) {
        if (blockState.is(Blocks.LAVA) || blockState.is(Blocks.MAGMA_BLOCK)
                || blockState.is(Blocks.CAMPFIRE) || blockState.is(Blocks.SOUL_CAMPFIRE)
                || blockState.is(ModTags.Blocks.LAVA_BLOCKS)) {
            return CoolRainSounds.RAIN_SOUNDS_LAVA.get();
        }

        if (blockState.is(Blocks.WATER) || blockState.is(ModTags.Blocks.WATER_BLOCKS)) {
            return CoolRainSounds.RAIN_SOUNDS_WATER.get();
        }

        if (blockState.is(Blocks.AMETHYST_BLOCK) || blockState.is(Blocks.AMETHYST_CLUSTER)
                || blockState.is(Blocks.BUDDING_AMETHYST) || blockState.is(Blocks.SMALL_AMETHYST_BUD)
                || blockState.is(Blocks.MEDIUM_AMETHYST_BUD) || blockState.is(Blocks.LARGE_AMETHYST_BUD)
                || blockState.is(ModTags.Blocks.AMETHYST_BLOCKS)) {
            return CoolRainSounds.RAIN_SOUNDS_AMETHYST.get();
        }

        if (blockState.is(Blocks.NOTE_BLOCK) || blockState.is(ModTags.Blocks.NOTEBLOCK_BLOCKS)) {
            return CoolRainSounds.RAIN_SOUNDS_NOTEBLOCK.get();
        }

        if (blockState.is(ModTags.Blocks.METAL_BLOCKS)
                || blockState.is(ModTags.Blocks.C_METAL_BLOCKS)
                || blockState.is(ModTags.Blocks.C_STORAGE_BLOCKS_IRON)
                || blockState.is(ModTags.Blocks.C_STORAGE_BLOCKS_COPPER)
                || blockState.is(ModTags.Blocks.C_STORAGE_BLOCKS_GOLD)
                || blockState.is(ModTags.Blocks.C_STORAGE_BLOCKS_NETHERITE)
                || blockState.is(Blocks.IRON_BLOCK) || blockState.is(Blocks.GOLD_BLOCK)
                || blockState.is(Blocks.NETHERITE_BLOCK) || blockState.is(Blocks.COPPER_BLOCK)
                || blockState.is(Blocks.RAW_IRON_BLOCK) || blockState.is(Blocks.RAW_COPPER_BLOCK)
                || blockState.is(Blocks.RAW_GOLD_BLOCK) || blockState.is(Blocks.LANTERN)
                || blockState.is(Blocks.SOUL_LANTERN) || blockState.is(Blocks.IRON_TRAPDOOR)) {
            return CoolRainSounds.RAIN_SOUNDS_METAL.get();
        }

        if (blockState.is(ModTags.Blocks.GLASS_BLOCKS)
                || blockState.is(ModTags.Blocks.C_GLASS_BLOCKS)
                || blockState.is(ModTags.Blocks.C_GLASS_PANES)
                || blockState.is(ModTags.Blocks.C_ICE)
                || blockState.is(Blocks.GLASS) || blockState.is(Blocks.TINTED_GLASS)
                || blockState.is(Blocks.ICE) || blockState.is(Blocks.BLUE_ICE) || blockState.is(Blocks.PACKED_ICE)) {
            return CoolRainSounds.RAIN_SOUNDS_GLASS.get();
        }

        if (blockState.is(ModTags.Blocks.FABRIC_BLOCKS)
                || blockState.is(BlockTags.WOOL) || blockState.is(BlockTags.WOOL_CARPETS)
                || blockState.is(BlockTags.BEDS)) {
            return CoolRainSounds.RAIN_SOUNDS_FABRIC.get();
        }

        if (blockState.is(ModTags.Blocks.FOLIAGE_BLOCKS)
                || blockState.is(ModTags.Blocks.C_LEAVES)
                || blockState.is(BlockTags.LEAVES)) {
            return CoolRainSounds.RAIN_SOUNDS_FOLIAGE.get();
        }

        return SoundEvents.WEATHER_RAIN;
    }

    @Unique
    private float getRainSoundVolume(BlockState blockState, boolean isMuffled) {
        SoundEvent sound = getRainSoundForBlock(blockState);

        if (sound == CoolRainSounds.RAIN_SOUNDS_FABRIC.get()) {
            return 0.75F;
        } else if (sound == CoolRainSounds.RAIN_SOUNDS_WATER.get()) {
            return 0.175F;
        } else if (sound == CoolRainSounds.RAIN_SOUNDS_AMETHYST.get()) {
            return 0.3F;
        } else if (sound == CoolRainSounds.RAIN_SOUNDS_NOTEBLOCK.get()) {
            return 0.2F;
        } else if (sound == CoolRainSounds.RAIN_SOUNDS_LAVA.get()) {
            return 0.2F;
        } else if (isMuffled) {
            return 0.1F;
        } else if (sound == SoundEvents.WEATHER_RAIN) {
            return 0.2F;
        } else {
            return 0.5F;
        }
    }

    @Unique
    private float getRainSoundPitch(BlockState blockState, RandomSource random) {
        SoundEvent sound = getRainSoundForBlock(blockState);

        if (sound == CoolRainSounds.RAIN_SOUNDS_FOLIAGE.get() || sound == CoolRainSounds.RAIN_SOUNDS_LAVA.get()) {
            return 1.0F;
        } else if (sound == SoundEvents.WEATHER_RAIN) {
            return 1.0F;
        } else {
            return random.nextIntBetweenInclusive(8, 12) * 0.1F;
        }
    }

    @Unique
    private boolean shouldAttenuateSound(BlockState blockState) {
        SoundEvent sound = getRainSoundForBlock(blockState);
        return sound != CoolRainSounds.RAIN_SOUNDS_AMETHYST.get()
                && sound != CoolRainSounds.RAIN_SOUNDS_NOTEBLOCK.get()
                && sound != SoundEvents.WEATHER_RAIN;
    }
}