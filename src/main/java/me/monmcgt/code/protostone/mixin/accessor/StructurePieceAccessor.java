package me.monmcgt.code.protostone.mixin.accessor;

import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockMirror;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * <a href="https://github.com/DeadlyMC/ProtoSky/blob/9b938afbddf9f5ad5db4099d6f5a43129ae34071/src/main/java/protosky/mixins/StructurePieceAccessor.java">Source</a>
 */
@Mixin(StructurePiece.class)
public interface StructurePieceAccessor {
    @Accessor("mirror")
    BlockMirror getMirror();

    @Invoker
    int invokeApplyXTransform(int x, int z);

    @Invoker
    int invokeApplyYTransform(int y);

    @Invoker
    int invokeApplyZTransform(int x, int z);
}
