package me.monmcgt.code.protostone.util;

import me.monmcgt.code.protostone.mixin.accessor.ChunkGeneratorAccessor;
import net.minecraft.SharedConstants;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <a href="https://github.com/DeadlyMC/ProtoSky/blob/9b938afbddf9f5ad5db4099d6f5a43129ae34071/src/main/java/protosky/stuctures/endCityHelper.java">Source</a>
 */
public class EndCityUtil {
    private static boolean ran = false;
    private static ConfiguredStructureFeature<?, ?> endCityFeature = null;

    private static synchronized void fixRaceCondition(WorldAccess world) {
        if (!ran) {
            endCityFeature = world.getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY).get(Identifier.tryParse("end_city"));
            ran = true;
        }
    }

    public static void generateEndCities(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, ChunkGenerator generator) {
        //Position Stuff
        ChunkPos chunkPos = chunk.getPos();
        if (!SharedConstants.method_37896(chunkPos)) {
            ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, world.getBottomSectionCoord());
            BlockPos blockPos = chunkSectionPos.getMinPos();

            //Registry Stuff
            Registry<ConfiguredStructureFeature<?, ?>> registry = world.getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
            Map<Integer, List<ConfiguredStructureFeature<?, ?>>> map = registry.stream()
                    .collect(Collectors.groupingBy(configuredStructureFeature -> configuredStructureFeature.feature.getGenerationStep().ordinal()));

            BiomeSource populationSource = ((ChunkGeneratorAccessor) generator).getPopulationSource();

            List<BiomeSource.IndexedFeatures> list = populationSource.getIndexedFeatures();
            ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));
            long l = chunkRandom.setPopulationSeed(world.getSeed(), blockPos.getX(), blockPos.getZ());
            int i = list.size();

            fixRaceCondition(world);

            try {
                int j = Math.max(GenerationStep.Feature.values().length, i);

                for (int k = 0; k < j; ++k) {
                    int m = 0;
                    if (structureAccessor.shouldGenerateStructures()) {
                        for (ConfiguredStructureFeature<?, ?> configuredStructureFeature : map.getOrDefault(k, Collections.emptyList())) {
                            chunkRandom.setDecoratorSeed(l, m, k);
                            Supplier<String> supplier = () -> (String) registry.getKey(configuredStructureFeature)
                                    .map(Object::toString)
                                    .orElseGet(configuredStructureFeature::toString);

                            try {
                                world.setCurrentlyGeneratingStructureName(supplier);
                                structureAccessor.getStructureStarts(chunkSectionPos, configuredStructureFeature)
                                        .forEach(
                                                structureStart -> {
                                                    if (structureStart.getFeature().equals(endCityFeature)) {
                                                        structureStart.place(
                                                                world, structureAccessor, generator, chunkRandom, ChunkGeneratorAccessor.getBlockBoxForChunkInvoker(chunk), chunkPos
                                                        );
                                                    }
                                                }
                                        );
                            } catch (Exception var29) {
                                CrashReport crashReport = CrashReport.create(var29, "Feature placement");
                                crashReport.addElement("Feature").add("Description", supplier::get);
                                throw new CrashException(crashReport);
                            }

                            ++m;
                        }
                    }
                }

                world.setCurrentlyGeneratingStructureName(null);
            } catch (Exception var31) {
                CrashReport crashReport3 = CrashReport.create(var31, "Biome decoration");
                crashReport3.addElement("Generation").add("CenterX", chunkPos.x).add("CenterZ", chunkPos.z).add("Seed", l);
                throw new CrashException(crashReport3);
            }
        }
    }
}
