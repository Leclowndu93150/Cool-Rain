package com.leclowndu93150.coolrain;

import com.leclowndu93150.coolrain.tags.ModTags;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = CoolRain.MODID, dist = Dist.CLIENT)
public class CoolRain {
    public static final String MODID = "coolrain";

    public CoolRain(IEventBus modEventBus, ModContainer modContainer) {
        CoolRainSounds.SOUND_EVENTS.register(modEventBus);
        ModTags.Blocks.registerTags();
    }

}
