package com.leclowndu93150.coolrain;

import com.leclowndu93150.coolrain.tags.ModTags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(value = CoolRain.MODID)
public class CoolRain {
    public static final String MODID = "coolrain";

    public CoolRain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        if(FMLLoader.getDist().isClient()){
            CoolRainSounds.SOUND_EVENTS.register(modEventBus);
            ModTags.Blocks.registerTags();
        }
    }

}
