package com.leclowndu93150.coolrain.mixin;

import com.leclowndu93150.coolrain.CoolRainSounds;
import com.leclowndu93150.coolrain.tags.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(WeatherEffectRenderer.class)
public abstract class RainMixin {

    @Shadow
    private int rainSoundTime;

    @Inject(method = "tickRainParticles", at = @At("HEAD"), cancellable = true)
    public void addParticlesAndSound(ClientLevel world, Camera camera, int ticks, ParticleStatus particlesMode, CallbackInfo ci) {
        float rainLevel = world.getRainLevel(1.0F) / (Minecraft.useFancyGraphics() ? 1.0F : 2.0F);
        if (rainLevel <= 0.0F) {
            return;
        }

        RandomSource random = RandomSource.create((long) ticks * 312987231L);
        BlockPos cameraPos = BlockPos.containing(camera.getPosition());
        BlockPos soundPos = null;
        int particleCount = (int) (100.0F * rainLevel * rainLevel) / (particlesMode == ParticleStatus.DECREASED ? 2 : 1);

        for (int i = 0; i < particleCount; i++) {
            int offsetX = random.nextInt(21) - 10;
            int offsetZ = random.nextInt(21) - 10;
            BlockPos heightmapPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPos.offset(offsetX, 0, offsetZ));
            
            if (heightmapPos.getY() > world.getMinY()
                    && heightmapPos.getY() <= cameraPos.getY() + 10
                    && heightmapPos.getY() >= cameraPos.getY() - 10
                    && getPrecipitationAt(world, heightmapPos) == Biome.Precipitation.RAIN) {
                
                soundPos = heightmapPos.below();
                if (particlesMode == ParticleStatus.MINIMAL) {
                    break;
                }

                double particleX = random.nextDouble();
                double particleZ = random.nextDouble();
                BlockState blockState = world.getBlockState(soundPos);
                FluidState fluidState = world.getFluidState(soundPos);
                VoxelShape shape = blockState.getCollisionShape(world, soundPos);
                double shapeHeight = shape.max(Direction.Axis.Y, particleX, particleZ);
                double fluidHeight = fluidState.getHeight(world, soundPos);
                double maxHeight = Math.max(shapeHeight, fluidHeight);
                
                ParticleOptions particleType = getParticleType(blockState, fluidState);
                world.addParticle(particleType, soundPos.getX() + particleX, soundPos.getY() + maxHeight, soundPos.getZ() + particleZ, 0.0, 0.0, 0.0);
            }
        }

        if (soundPos != null && random.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            BlockState blockState = world.getBlockState(soundPos);
            boolean isMuffled = isMuffledSound(world, soundPos, cameraPos);
            
            playRainSound(world, soundPos, blockState, random, isMuffled);
        }

        ci.cancel();
    }

    @Unique
    private ParticleOptions getParticleType(BlockState blockState, FluidState fluidState) {
        return !fluidState.is(FluidTags.LAVA) && !blockState.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(blockState)
                ? ParticleTypes.RAIN
                : ParticleTypes.SMOKE;
    }

    @Unique
    private boolean isMuffledSound(ClientLevel world, BlockPos soundPos, BlockPos cameraPos) {
        return soundPos.getY() > cameraPos.getY() + 1
                && world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPos).getY() > Mth.floor((float) cameraPos.getY());
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
        // Priority order: specific blocks -> mod tags -> common tags -> vanilla tags -> default

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
        } else if (sound == SoundEvents.WEATHER_RAIN_ABOVE) {
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
        } else if (sound == SoundEvents.WEATHER_RAIN_ABOVE) {
            return 0.75F;
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
                && sound != SoundEvents.WEATHER_RAIN_ABOVE
                && sound != SoundEvents.WEATHER_RAIN;
    }

    @Unique
    private Biome.Precipitation getPrecipitationAt(Level world, BlockPos pos) {
        if (!world.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) {
            return Biome.Precipitation.NONE;
        }
        Biome biome = world.getBiome(pos).value();
        return biome.getPrecipitationAt(pos, world.getSeaLevel());
    }
}