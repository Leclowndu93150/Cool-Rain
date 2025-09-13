package com.leclowndu93150.coolrain;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CoolRainSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CoolRain.MODID);

    public static final Supplier<SoundEvent> RAIN_SOUNDS_METAL = registerSoundEvent("rain_sounds_metal");
    public static final Supplier<SoundEvent> RAIN_SOUNDS_FOLIAGE = registerSoundEvent("rain_sounds_foliage");
    public static final Supplier<SoundEvent> RAIN_SOUNDS_FABRIC = registerSoundEvent("rain_sounds_fabric");
    public static final Supplier<SoundEvent> RAIN_SOUNDS_GLASS = registerSoundEvent("rain_sounds_glass");
    public static final Supplier<SoundEvent> RAIN_SOUNDS_WATER = registerSoundEvent("rain_sounds_water");
    public static final Supplier<SoundEvent> RAIN_SOUNDS_LAVA = registerSoundEvent("rain_sounds_lava");
    public static final Supplier<SoundEvent> RAIN_SOUNDS_AMETHYST = registerSoundEvent("rain_sounds_amethyst");
    public static final Supplier<SoundEvent> RAIN_SOUNDS_NOTEBLOCK = registerSoundEvent("rain_sounds_noteblock");

    public static final Supplier<SoundEvent> RAIN_SOUNDS_METAL_MUFFLED = registerSoundEvent("rain_sounds_metal_muffled");
    public static final Supplier<SoundEvent> RAIN_SOUNDS_FABRIC_MUFFLED = registerSoundEvent("rain_sounds_fabric_muffled");
    public static final Supplier<SoundEvent> RAIN_SOUNDS_GLASS_MUFFLED = registerSoundEvent("rain_sounds_glass_muffled");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CoolRain.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
