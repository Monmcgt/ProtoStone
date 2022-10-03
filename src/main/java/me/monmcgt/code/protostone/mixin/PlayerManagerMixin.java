package me.monmcgt.code.protostone.mixin;

import me.monmcgt.code.protostone.util.DimensionTypeUtil;
import me.monmcgt.code.protostone.util.PlayerManagerUtil;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerManager playerManager = (PlayerManager) (Object) this;

        ServerWorld serverWorld = player.getWorld();
        boolean isOverworld = DimensionTypeUtil.isOverworld(serverWorld);
        BlockPos pos = null;
        if (isOverworld) {
            Identifier id = serverWorld.getRegistryKey().getValue();
            pos = PlayerManagerUtil.identifierBlockPosMap.get(id);
        }

        boolean isFirstTimeJoin = playerManager.loadPlayerData(player) == null;

        if (isFirstTimeJoin && pos != null) {
            player.teleport(pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
