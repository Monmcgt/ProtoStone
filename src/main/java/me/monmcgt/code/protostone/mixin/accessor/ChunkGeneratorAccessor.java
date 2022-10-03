package me.monmcgt.code.protostone.mixin.accessor;

import net.minecraft.util.math.BlockBox;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * <a href="https://github.com/DeadlyMC/ProtoSky/blob/9b938afbddf9f5ad5db4099d6f5a43129ae34071/src/main/java/protosky/mixins/endCityParts/ChunkGeneratorMixin.java">Source</a>
 */
@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
    @Accessor("populationSource")
    BiomeSource getPopulationSource();

    @Invoker("getBlockBoxForChunk")
    static BlockBox getBlockBoxForChunkInvoker(Chunk chunk) {
        throw new AssertionError();
    }
}
