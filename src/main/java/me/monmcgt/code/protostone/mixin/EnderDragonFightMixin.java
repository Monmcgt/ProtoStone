package me.monmcgt.code.protostone.mixin;

import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {
    @Shadow
    private BlockPos exitPortalLocation;

    @Inject(method = "generateEndPortal(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/EndPortalFeature;generateIfValid(Lnet/minecraft/world/gen/feature/FeatureConfig;Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;)Z", shift = At.Shift.BEFORE))
    private void generateEndPortalMixin(boolean shouldGenerate, CallbackInfo ci) {
        if (this.exitPortalLocation == null) {
            this.exitPortalLocation = new BlockPos(0, 0, 0);
        }

        this.exitPortalLocation = new BlockPos(this.exitPortalLocation.getX(), 60, this.exitPortalLocation.getZ());
    }
}