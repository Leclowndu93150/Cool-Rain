package com.leclowndu93150.coolrain.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {

        // Mod specific tags
        public static final TagKey<Block> METAL_BLOCKS =
                createTag("metal_rain_blocks");

        public static final TagKey<Block> FOLIAGE_BLOCKS =
                createTag("foliage_rain_blocks");

        public static final TagKey<Block> FABRIC_BLOCKS =
                createTag("fabric_rain_blocks");

        public static final TagKey<Block> GLASS_BLOCKS =
                createTag("glass_rain_blocks");

        public static final TagKey<Block> WATER_BLOCKS =
                createTag("water_rain_blocks");

        public static final TagKey<Block> AMETHYST_BLOCKS =
                createTag("amethyst_rain_blocks");

        public static final TagKey<Block> NOTEBLOCK_BLOCKS =
                createTag("noteblock_rain_blocks");

        public static final TagKey<Block> LAVA_BLOCKS =
                createTag("lava_rain_blocks");

        // Forge tags
        public static final TagKey<Block> FORGE_GLASS_BLOCKS =
                createForgeTag("glass");

        public static final TagKey<Block> FORGE_GLASS_PANES =
                createForgeTag("glass_panes");

        public static final TagKey<Block> FORGE_STORAGE_BLOCKS =
                createForgeTag("storage_blocks");

        public static final TagKey<Block> FORGE_STORAGE_BLOCKS_IRON =
                createForgeTag("storage_blocks/iron");

        public static final TagKey<Block> FORGE_STORAGE_BLOCKS_COPPER =
                createForgeTag("storage_blocks/copper");

        public static final TagKey<Block> FORGE_STORAGE_BLOCKS_GOLD =
                createForgeTag("storage_blocks/gold");

        public static final TagKey<Block> FORGE_STORAGE_BLOCKS_NETHERITE =
                createForgeTag("storage_blocks/netherite");

        public static final TagKey<Block> FORGE_ORES =
                createForgeTag("ores");

        private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK,new ResourceLocation("coolrain", name));
        }

        private static TagKey<Block> createForgeTag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation("forge", name));
        }

        public static void registerTags() {
        }
    }

}
