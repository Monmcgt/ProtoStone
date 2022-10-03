package me.monmcgt.code.protostone.mixin;

import me.monmcgt.code.protostone.util.ChunkStatusUtil;
import me.monmcgt.code.protostone.util.ChunkStatusUtil.Nether;
import me.monmcgt.code.protostone.util.ChunkStatusUtil.Overworld;
import me.monmcgt.code.protostone.util.ChunkStatusUtil.TheEnd;
import me.monmcgt.code.protostone.util.DimensionTypeUtil;
import me.monmcgt.code.protostone.util.EndCityUtil;
import me.monmcgt.code.protostone.util.StructureUtil;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
                case OVERWORLD -> {
                    Overworld.addStones(protoChunk);
//                    Overworld.genHeightMaps(protoChunk);
                }
                case NETHER -> Nether.addNetherrack(protoChunk);
                case THE_END -> {
                    TheEnd.addEndStone(protoChunk);
//                    TheEnd.genHeightMaps(protoChunk);
                }
            }

//            LogUtils.getLogger().info("ChunkStatusUtil: " + dimensionType.name() + " chunk at " + chunk.getPos().x + ", " + chunk.getPos().z + " has been modified.");

            if (ChunkStatusUtil.isSpawnChunk(chunk, world)) {
                switch (dimensionType) {
                    case OVERWORLD -> Overworld.genSpawnPlatformAir(protoChunk, world);
//                    case NETHER -> ChunkStatusUtil.Nether.genSpawnPlatformAir(protoChunk, world);
//                    case THE_END -> ChunkStatusUtil.TheEnd.genSpawnPlatformAir(protoChunk, world);
                }
            }

            ChunkRegion chunkRegion = new ChunkRegion(world, chunks, targetStatus, 1);

            EndCityUtil.generateEndCities(chunkRegion, chunk, world.getStructureAccessor().forRegion(chunkRegion), generator);
            StructureUtil.genStructures((ProtoChunk) chunk, world, structureManager, generator);

            ChunkStatusUtil.clearEntities(protoChunk, world);
        }
    }

//    @Inject(method = "method_16569", at = @At("HEAD"), cancellable = true)
    private static void SURFACE_REGISTER_LAMBDA(ChunkStatus targetStatus, ServerWorld world, ChunkGenerator generator, List chunks, Chunk chunk, CallbackInfo ci) {
        ci.cancel();
    }

//    @Inject(method = "method_17033", at = @At("HEAD"), cancellable = true)
    private static void SPAWN_REGISTER_LAMBDA(ChunkStatus targetStatus, ServerWorld world, ChunkGenerator generator, List chunks, Chunk chunk, CallbackInfo ci) {
        ci.cancel();
    }

//    @Inject(method = "method_38282", at = @At("HEAD"), cancellable = true)
    private static void CARVERS_REGISTER_LAMBDA(ChunkStatus targetStatus, ServerWorld world, ChunkGenerator generator, List chunks, Chunk chunk, CallbackInfo ci) {
        ci.cancel();
    }

//    @Inject(method = "method_39789", at = @At("HEAD"), cancellable = true)
    private static void LIQUID_CARVERS_REGISTER_LAMBDA(ChunkStatus targetStatus, ServerWorld world, ChunkGenerator generator, List chunks, Chunk chunk, CallbackInfo ci) {
        ci.cancel();
    }

    //    @Inject(method = "method_38284", at = @At("HEAD"), cancellable = true)
    private static void NOISE_REGISTER_LAMBDA(ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function function, List chunks, Chunk chunk, boolean bl, CallbackInfoReturnable<CompletableFuture> cir) {
    }
}
