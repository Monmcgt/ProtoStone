package me.monmcgt.code.protostone.util;

import me.monmcgt.code.protostone.mixin.accessor.DimensionTypeAccessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class PlayerManagerUtil {
    public static Map<Identifier, BlockPos> identifierBlockPosMap = new HashMap<>();

    public static int getShouldSpawnHeight(World world) {
        int x;
        int yReal;
        int z;
        Identifier id = world.getRegistryKey().getValue();
        BlockPos pos = identifierBlockPosMap.get(id);
        if (pos != null) {
            x = pos.getX();
            yReal = pos.getY();
            z = pos.getZ();
        } else {
            x = world.getLevelProperties().getSpawnX();
            yReal = world.getLevelProperties().getSpawnY();
            z = world.getLevelProperties().getSpawnZ();
        }
        System.out.println("x: " + x + " y: " + yReal + " z: " + z);
        if (!isAirBlock(world, new BlockPos(x, yReal - 1, z)) && isAirBlock(world, new BlockPos(x, yReal, z)) && isAirBlock(world, new BlockPos(x, yReal + 1, z))) {
            System.out.println("return 1");
            return yReal;
        }
        // if not and the block below is air start searching for the first block that is not air
        int notAir = yReal;
        for (int i = yReal; i > 0; i--) {
            if (!isAirBlock(world, new BlockPos(x, i, z))) {
                System.out.println("Found not air at position x" + x + " y" + i + " z" + z);
                notAir = i;
                break;
            }
        }
        if (isAirBlock(world, new BlockPos(x, notAir + 1, z)) && isAirBlock(world, new BlockPos(x, notAir + 2, z))) {
            System.out.println("return 2");
            return notAir + 1;
        } else {
            System.out.println("block 1 : " + isAirBlock(world, new BlockPos(x, notAir + 1, z)));
            System.out.println("block 2 : " + isAirBlock(world, new BlockPos(x, notAir + 2, z)));
        }
        // if not start searching upwards
        for (int i = notAir; i < ((DimensionTypeAccessor) world.getDimension()).getHeight(); i++) {
            if (isAirBlock(world, new BlockPos(x, i, z)) && isAirBlock(world, new BlockPos(x, i + 1, z)) && isAirBlock(world, new BlockPos(x, i + 2, z))) {
                System.out.println("return 3");
                return i;
            }
        }
        System.out.println("return 4");
        return ((DimensionTypeAccessor) world.getDimension()).getHeight() - 1;
    }

    public static boolean isAirBlock(World world, BlockPos pos) {
        return world.getBlockState(pos).isAir();
    }
}
