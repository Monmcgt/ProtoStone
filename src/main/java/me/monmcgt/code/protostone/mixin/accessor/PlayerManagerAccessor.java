package me.monmcgt.code.protostone.mixin.accessor;

import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerManager.class)
public interface PlayerManagerAccessor {
    @Accessor("server")
    MinecraftServer getServer();

    @Accessor("registryManager")
    DynamicRegistryManager.Immutable getRegistryManager();

    @Accessor("players")
    List<ServerPlayerEntity> getPlayers();

    @Accessor("playerMap")
    Map<UUID, ServerPlayerEntity> getPlayerMap();

    @Invoker("sendScoreboard")
    void invokeSendScoreboard(ServerScoreboard scoreboard, ServerPlayerEntity player);
}
