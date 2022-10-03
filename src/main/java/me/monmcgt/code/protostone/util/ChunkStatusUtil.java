package me.monmcgt.code.protostone.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.ProtoChunk;

import java.util.List;

public class ChunkStatusUtil {
    public static boolean isSpawnChunk(Chunk chunk, ServerWorld world) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos blockPos = new BlockPos(chunkPos.x * 16, 64, chunkPos.z * 16);
        return world.getSpawnPos().isWithinDistance(blockPos, 16);
    }

    public static class Overworld {
        public static final List<Block> BLOCK_TO_REPLACE = List.of(Blocks.AIR, Blocks.WATER, Blocks.LAVA);

        public static void addStones(ProtoChunk chunk) {
            ChunkSection[] sections = chunk.getSectionArray();
            try {
                for (int i = 0; i < sections.length; i++) {
                    ChunkSection chunkSection = sections[i];
                    PalettedContainer<BlockState> blockStateContainer = chunkSection.getBlockStateContainer();
                    PalettedContainer<RegistryEntry<Biome>> biomeContainer = chunkSection.getBiomeContainer();
                    int chunkPos = chunkSection.getYOffset() >> 4;
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < 16; y++) {
                                if (BLOCK_TO_REPLACE.contains(blockStateContainer.get(x, y, z).getBlock())) {
                                    blockStateContainer.set(x, y, z, Blocks.STONE.getDefaultState());
                                }
                            }
                        }
                    }
                    sections[i] = new ChunkSection(chunkPos, blockStateContainer, biomeContainer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Remove all blocks 16*16*8 above the spawn platform
         */
        public static void genSpawnPlatformAir(Chunk chunk, ServerWorld world) {
            ChunkPos chunkPos = chunk.getPos();
            BlockPos blockPos = new BlockPos(chunkPos.x * 16, 64, chunkPos.z * 16);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 8; y++) {
                        chunk.setBlockState(new BlockPos(x, y + 64, z), Blocks.AIR.getDefaultState(), false);
                    }
                }
            }

            Identifier id = world.getRegistryKey().getValue();
            PlayerManagerUtil.identifierBlockPosMap.remove(id);
            PlayerManagerUtil.identifierBlockPosMap.put(id, new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()));
        }
    }

    public static class Nether {
        public static final List<Block> BLOCK_TO_REPLACE = List.of(Blocks.AIR, Blocks.LAVA);

        public static void addNetherrack(Chunk chunk) {
            ChunkSection[] sections = chunk.getSectionArray();
            try {
                for (int i = 0; i < sections.length; i++) {
                    ChunkSection chunkSection = sections[i];
                    PalettedContainer<BlockState> blockStateContainer = chunkSection.getBlockStateContainer();
                    PalettedContainer<RegistryEntry<Biome>> biomeContainer = chunkSection.getBiomeContainer();
                    int chunkPos = chunkSection.getYOffset() >> 4;
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < 16; y++) {
                                if (BLOCK_TO_REPLACE.contains(blockStateContainer.get(x, y, z).getBlock())) {
                                    // if not higher than 128
                                    if (chunkSection.getYOffset() + y < 128) {
                                        blockStateContainer.set(x, y, z, Blocks.NETHERRACK.getDefaultState());
                                    }
                                }
                            }
                        }
                    }
                    sections[i] = new ChunkSection(chunkPos, blockStateContainer, biomeContainer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void genSpawnPlatformAir(Chunk chunk, ServerWorld world) {
            ChunkPos chunkPos = chunk.getPos();
            BlockPos blockPos = new BlockPos(chunkPos.x * 16, 64, chunkPos.z * 16);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 8; y++) {
                        chunk.setBlockState(new BlockPos(x, y + 64, z), Blocks.AIR.getDefaultState(), false);
                    }
                }
            }

            Identifier id = world.getRegistryKey().getValue();
            PlayerManagerUtil.identifierBlockPosMap.remove(id);
            PlayerManagerUtil.identifierBlockPosMap.put(id, new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()));
        }
    }

    public static class TheEnd {
        public static final List<Block> BLOCK_TO_REPLACE = List.of(Blocks.AIR);

        public static void addEndStone(Chunk chunk) {
            ChunkSection[] sections = chunk.getSectionArray();
            try {
                for (int i = 0; i < sections.length; i++) {
                    ChunkSection chunkSection = sections[i];
                    PalettedContainer<BlockState> blockStateContainer = chunkSection.getBlockStateContainer();
                    PalettedContainer<RegistryEntry<Biome>> biomeContainer = chunkSection.getBiomeContainer();
                    int chunkPos = chunkSection.getYOffset() >> 4;
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < 16; y++) {
                                BlockState blockState = blockStateContainer.get(x, y, z);
                                if (BLOCK_TO_REPLACE.contains(blockState.getBlock())) {
                                    blockStateContainer.set(x, y, z, Blocks.END_STONE.getDefaultState());
                                }
                            }
                        }
                    }
                    sections[i] = new ChunkSection(chunkPos, blockStateContainer, biomeContainer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void genSpawnPlatformAir(Chunk chunk, ServerWorld world) {
            ChunkPos chunkPos = chunk.getPos();
            BlockPos blockPos = new BlockPos(chunkPos.x * 16, 64, chunkPos.z * 16);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 8; y++) {
                        chunk.setBlockState(new BlockPos(x, y + 64, z), Blocks.AIR.getDefaultState(), false);
                    }
                }
            }

            Identifier id = world.getRegistryKey().getValue();
            PlayerManagerUtil.identifierBlockPosMap.remove(id);
            PlayerManagerUtil.identifierBlockPosMap.put(id, new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()));
        }
    }
}
