package me.monmcgt.code.protostone.mixin;

import me.monmcgt.code.protostone.util.ChunkStatusUtil;
import me.monmcgt.code.protostone.util.DimensionTypeUtil;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {
    @Inject(method = "method_20613", at = @At("HEAD"))
    private static void FEATURES_REGISTER_LAMBDA(ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function function, List chunks, Chunk chunk, boolean bl, CallbackInfoReturnable<CompletableFuture> cir) {
        if (!chunk.getStatus().isAtLeast(targetStatus)) {
            ProtoChunk protoChunk = (ProtoChunk) chunk;
            protoChunk.setLightingProvider(lightingProvider);

            DimensionTypeUtil.DimensionTypeEnum dimensionType = DimensionTypeUtil.getDimensionType(world);

            switch (dimensionType) {
                case OVERWORLD -> ChunkStatusUtil.Overworld.addStones(protoChunk);
                case NETHER -> ChunkStatusUtil.Nether.addNetherrack(protoChunk);
                case THE_END -> ChunkStatusUtil.TheEnd.addEndStone(protoChunk);
            }

//            LogUtils.getLogger().info("ChunkStatusUtil: " + dimensionType.name() + " chunk at " + chunk.getPos().x + ", " + chunk.getPos().z + " has been modified.");

            if (ChunkStatusUtil.isSpawnChunk(chunk, world)) {
                switch (dimensionType) {
                    case OVERWORLD -> ChunkStatusUtil.Overworld.genSpawnPlatformAir(protoChunk, world);
//                    case NETHER -> ChunkStatusUtil.Nether.genSpawnPlatformAir(protoChunk, world);
//                    case THE_END -> ChunkStatusUtil.TheEnd.genSpawnPlatformAir(protoChunk, world);
                }
            }
        }
    }
}
