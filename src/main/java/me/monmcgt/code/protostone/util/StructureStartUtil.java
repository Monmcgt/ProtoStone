package me.monmcgt.code.protostone.util;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureStartUtil {
    public static void removeEndstones(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.END_STONE) {
                        world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }
}
