package me.monmcgt.code.protostone.mixin.accessor;

import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureStart.class)
public interface StructureStartAccessor {
    @Accessor("children")
    StructurePiecesList getChildren();
}