package me.monmcgt.code.protostone.util;

import me.monmcgt.code.protostone.mixin.accessor.DimensionTypeAccessor;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class DimensionTypeUtil {
    public static boolean isOverworld(DimensionType dimensionType) {
        return dimensionType == DimensionTypeAccessor.getOverworld();
    }

    public static boolean isOverworld(World world) {
        return isOverworld(world.getDimension());
    }

    public static boolean isNether(DimensionType dimensionType) {
        return dimensionType == DimensionTypeAccessor.getNether();
    }

    public static boolean isNether(World world) {
        return isNether(world.getDimension());
    }

    public static boolean isTheEnd(DimensionType dimensionType) {
        return dimensionType == DimensionTypeAccessor.getTheEnd();
    }

    public static boolean isTheEnd(World world) {
        return isTheEnd(world.getDimension());
    }

    public static DimensionTypeEnum getDimensionType(World world) {
        if (isOverworld(world)) {
            return DimensionTypeEnum.OVERWORLD;
        } else if (isNether(world)) {
            return DimensionTypeEnum.NETHER;
        } else if (isTheEnd(world)) {
            return DimensionTypeEnum.THE_END;
        } else {
            return DimensionTypeEnum.UNKNOWN;
        }
    }

    public enum DimensionTypeEnum {
        OVERWORLD,
        NETHER,
        THE_END,
        UNKNOWN
    }
}
