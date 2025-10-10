package com.leclowndu93150.coolrain;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CoolRainSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CoolRain.MODID);

    public static final RegistryObject<SoundEvent> RAIN_SOUNDS_METAL = registerSoundEvent("rain_sounds_metal");
    public static final RegistryObject<SoundEvent> RAIN_SOUNDS_FOLIAGE = registerSoundEvent("rain_sounds_foliage");
    public static final RegistryObject<SoundEvent> RAIN_SOUNDS_FABRIC = registerSoundEvent("rain_sounds_fabric");
    public static final RegistryObject<SoundEvent> RAIN_SOUNDS_GLASS = registerSoundEvent("rain_sounds_glass");
    public static final RegistryObject<SoundEvent> RAIN_SOUNDS_WATER = registerSoundEvent("rain_sounds_water");
    public static final RegistryObject<SoundEvent> RAIN_SOUNDS_LAVA = registerSoundEvent("rain_sounds_lava");
    public static final RegistryObject<SoundEvent> RAIN_SOUNDS_AMETHYST = registerSoundEvent("rain_sounds_amethyst");
    public static final RegistryObject<SoundEvent> RAIN_SOUNDS_NOTEBLOCK = registerSoundEvent("rain_sounds_noteblock");

    public static final RegistryObject<SoundEvent> RAIN_SOUNDS_METAL_MUFFLED = registerSoundEvent("rain_sounds_metal_muffled");
    public static final RegistryObject<SoundEvent> RAIN_SOUNDS_FABRIC_MUFFLED = registerSoundEvent("rain_sounds_fabric_muffled");
    public static final RegistryObject<SoundEvent> RAIN_SOUNDS_GLASS_MUFFLED = registerSoundEvent("rain_sounds_glass_muffled");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(CoolRain.MODID, name);
        return SOUND_EVENTS.register(name, () -> new SoundEvent(id));
    }
}
