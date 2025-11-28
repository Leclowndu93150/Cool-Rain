package com.leclowndu93150.coolrain.mixin;

import com.leclowndu93150.coolrain.CoolRainSounds;
import com.leclowndu93150.coolrain.tags.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WeatherEffectRenderer.class)
public abstract class RainMixin {

    @Redirect(
            method = "tickRainParticles",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V")
    )
    private void redirectRainSound(ClientLevel world, BlockPos pos, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay) {
        BlockPos cameraPos = BlockPos.containing(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
        BlockState blockState = world.getBlockState(pos);
        RandomSource random = world.getRandom();

        boolean isMuffled = pos.getY() > cameraPos.getY() + 1
                && world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPos).getY() > Mth.floor((float) cameraPos.getY());

        SoundEvent rainSound = getRainSoundForBlock(blockState, isMuffled);
        float rainVolume = getRainSoundVolume(rainSound, isMuffled);
        float rainPitch = getRainSoundPitch(rainSound, random);
        boolean shouldAttenuate = shouldAttenuateSound(rainSound);

        if (rainSound == CoolRainSounds.RAIN_SOUNDS_AMETHYST.get() || rainSound == CoolRainSounds.RAIN_SOUNDS_NOTEBLOCK.get()) {
            world.playLocalSound(pos, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
        }

        if (rainSound == CoolRainSounds.RAIN_SOUNDS_LAVA.get()) {
            world.playLocalSound(pos, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.1F, rainPitch, false);
        }

        world.playLocalSound(pos, rainSound, SoundSource.WEATHER, rainVolume, rainPitch, shouldAttenuate);
    }

    @Unique
    private SoundEvent getRainSoundForBlock(BlockState blockState, boolean isMuffled) {
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

        if (isMuffled) {
            return SoundEvents.WEATHER_RAIN_ABOVE;
        }

        return SoundEvents.WEATHER_RAIN;
    }

    @Unique
    private float getRainSoundVolume(SoundEvent sound, boolean isMuffled) {
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
        } else if (sound == SoundEvents.WEATHER_RAIN_ABOVE) {
            return 0.1F;
        } else if (sound == SoundEvents.WEATHER_RAIN) {
            return 0.2F;
        } else {
            return 0.5F;
        }
    }

    @Unique
    private float getRainSoundPitch(SoundEvent sound, RandomSource random) {
        if (sound == CoolRainSounds.RAIN_SOUNDS_FOLIAGE.get() || sound == CoolRainSounds.RAIN_SOUNDS_LAVA.get()) {
            return 1.0F;
        } else if (sound == SoundEvents.WEATHER_RAIN_ABOVE) {
            return 0.5F;
        } else if (sound == SoundEvents.WEATHER_RAIN) {
            return 1.0F;
        } else {
            return random.nextIntBetweenInclusive(8, 12) * 0.1F;
        }
    }

    @Unique
    private boolean shouldAttenuateSound(SoundEvent sound) {
        return sound != CoolRainSounds.RAIN_SOUNDS_AMETHYST.get()
                && sound != CoolRainSounds.RAIN_SOUNDS_NOTEBLOCK.get()
                && sound != SoundEvents.WEATHER_RAIN_ABOVE
                && sound != SoundEvents.WEATHER_RAIN;
    }
}