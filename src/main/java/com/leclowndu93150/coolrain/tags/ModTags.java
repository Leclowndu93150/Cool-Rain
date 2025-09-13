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

        // Common (c:) tags
        public static final TagKey<Block> C_GLASS_BLOCKS =
                createCommonTag("glass_blocks");

        public static final TagKey<Block> C_GLASS_PANES =
                createCommonTag("glass_panes");

        public static final TagKey<Block> C_METAL_BLOCKS =
                createCommonTag("metal_blocks");

        public static final TagKey<Block> C_ORES =
                createCommonTag("ores");

        public static final TagKey<Block> C_STORAGE_BLOCKS =
                createCommonTag("storage_blocks");

        public static final TagKey<Block> C_STORAGE_BLOCKS_IRON =
                createCommonTag("storage_blocks/iron");

        public static final TagKey<Block> C_STORAGE_BLOCKS_COPPER =
                createCommonTag("storage_blocks/copper");

        public static final TagKey<Block> C_STORAGE_BLOCKS_GOLD =
                createCommonTag("storage_blocks/gold");

        public static final TagKey<Block> C_STORAGE_BLOCKS_NETHERITE =
                createCommonTag("storage_blocks/netherite");

        public static final TagKey<Block> C_LEAVES =
                createCommonTag("leaves");

        public static final TagKey<Block> C_WOODEN_BLOCKS =
                createCommonTag("wooden_blocks");

        public static final TagKey<Block> C_ICE =
                createCommonTag("ice");

        private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("coolrain", name));
        }

        private static TagKey<Block> createCommonTag(String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", name));
        }

        public static void registerTags() {
        }
    }

}
