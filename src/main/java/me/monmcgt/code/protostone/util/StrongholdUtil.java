package me.monmcgt.code.protostone.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

import java.util.Random;

import static me.monmcgt.code.protostone.util.StructureUtil.isIntersecting;
import static me.monmcgt.code.protostone.util.StructureUtil.setBlockInStructure;

/**
 * <a href="https://github.com/DeadlyMC/ProtoSky/blob/9b938afbddf9f5ad5db4099d6f5a43129ae34071/src/main/java/protosky/stuctures/StrongHoldHelper.java">Source</a>
 */
public class StrongholdUtil {
    public static void genEndPortal(ProtoChunk chunk, StrongholdGenerator.PortalRoom room, Random random) {
        BlockState northFrame = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.NORTH);
        BlockState southFrame = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.SOUTH);
        BlockState eastFrame = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.EAST);
        BlockState westFrame = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.WEST);

        setBlockInStructure(room, chunk, northFrame, 4, 3, 8);
        setBlockInStructure(room, chunk, northFrame, 5, 3, 8);
        setBlockInStructure(room, chunk, northFrame, 6, 3, 8);
        setBlockInStructure(room, chunk, southFrame, 4, 3, 12);
        setBlockInStructure(room, chunk, southFrame, 5, 3, 12);
        setBlockInStructure(room, chunk, southFrame, 6, 3, 12);
        setBlockInStructure(room, chunk, eastFrame, 3, 3, 9);
        setBlockInStructure(room, chunk, eastFrame, 3, 3, 10);
        setBlockInStructure(room, chunk, eastFrame, 3, 3, 11);
        setBlockInStructure(room, chunk, westFrame, 7, 3, 9);
        setBlockInStructure(room, chunk, westFrame, 7, 3, 10);
        setBlockInStructure(room, chunk, westFrame, 7, 3, 11);
    }

    private static boolean ran = false;
    private static ConfiguredStructureFeature<?, ?> strongHoldFeature = null;

    private static synchronized void fixRaceCondition(WorldAccess world) {
        if (!ran) {
            strongHoldFeature = world.getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY).get(Identifier.tryParse("stronghold"));
            ran = true;
        }
    }

    public static void processStronghold(WorldAccess world, ProtoChunk chunk) {
        if (!ran) {
            fixRaceCondition(world);
        }

        for (long startPosLong : chunk.getStructureReferences(strongHoldFeature)) {
            ChunkPos startPos = new ChunkPos(startPosLong);
            ProtoChunk startChunk = (ProtoChunk) world.getChunk(startPos.x, startPos.z, ChunkStatus.STRUCTURE_STARTS);
            StructureStart stronghold = startChunk.getStructureStart(strongHoldFeature);

            if (stronghold != null && isIntersecting(stronghold, chunk)) {
                ChunkPos pos = chunk.getPos();
                for (Object piece : stronghold.getChildren()) {
                    if (((StructurePiece) piece).getBoundingBox().intersectsXZ(pos.getStartX(), pos.getStartZ(), pos.getEndX(), pos.getEndZ())) {
                        if (piece instanceof StrongholdGenerator.PortalRoom)
                            genEndPortal(chunk, (StrongholdGenerator.PortalRoom) piece, new Random(startPosLong));
                    }
                }
            }
        }
    }
}
