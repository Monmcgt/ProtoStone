package me.monmcgt.code.protostone.mixin;

import me.monmcgt.code.protostone.mixin.accessor.StructureStartAccessor;
import me.monmcgt.code.protostone.util.StructureStartUtil;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

@Mixin(StructureStart.class)
public abstract class StructureStartMixin {
//    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void onPlace(StructureWorldAccess structureWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, CallbackInfo ci) {
        StructureStart instance = (StructureStart) (Object) this;

        List<StructurePiece> list = instance.getChildren();
        if (!list.isEmpty()) {
            BlockBox blockBox = list.get(0).getBoundingBox();
            BlockPos blockPos = blockBox.getCenter();
            BlockPos blockPos2 = new BlockPos(blockPos.getX(), blockBox.getMinY(), blockPos.getZ());

            for (StructurePiece structurePiece : list) {
                BlockBox structurePieceBoundingBox = structurePiece.getBoundingBox();
                if (structurePieceBoundingBox.intersects(chunkBox)) {
                    structurePiece.generate(structureWorldAccess, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, blockPos2);

                    int minX = structurePieceBoundingBox.getMinX();
                    int minY = structurePieceBoundingBox.getMinY();
                    int minZ = structurePieceBoundingBox.getMinZ();
                    int maxX = structurePieceBoundingBox.getMaxX();
                    int maxY = structurePieceBoundingBox.getMaxY();
                    int maxZ = structurePieceBoundingBox.getMaxZ();

                    try {
                        StructureStartUtil.removeEndstones(structureWorldAccess.toServerWorld(), minX, minY, minZ, maxX, maxY, maxZ);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            instance.getFeature().feature.getPostProcessor().afterPlace(structureWorldAccess, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, ((StructureStartAccessor) this).getChildren());
        }

        ci.cancel();
    }
}
