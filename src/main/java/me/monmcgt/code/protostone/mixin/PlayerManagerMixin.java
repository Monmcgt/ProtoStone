package me.monmcgt.code.protostone.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import io.netty.buffer.Unpooled;
import me.monmcgt.code.protostone.mixin.accessor.PlayerManagerAccessor;
import me.monmcgt.code.protostone.util.DimensionTypeUtil;
import me.monmcgt.code.protostone.util.PlayerManagerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.TagPacketSerializer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "onPlayerConnect", at = @At("HEAD"), cancellable = true)
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerManagerAccessor t = (PlayerManagerAccessor) this;
        PlayerManager playerManager = (PlayerManager) (Object) this;

        GameProfile gameProfile = player.getGameProfile();
        UserCache userCache = t.getServer().getUserCache();
        Optional<GameProfile> optional = userCache.getByUuid(gameProfile.getId());
        String string = optional.map(GameProfile::getName).orElse(gameProfile.getName());
        userCache.add(gameProfile);
        NbtCompound nbtCompound = playerManager.loadPlayerData(player);
        RegistryKey registryKey1;
        boolean isFirstTimeJoin = nbtCompound == null;
        if (!isFirstTimeJoin) {
            DataResult dimension = DimensionType.worldFromDimensionNbt(new Dynamic(NbtOps.INSTANCE, nbtCompound.get("Dimension")));
            Logger logger = LOGGER;
            Objects.requireNonNull(logger);
            registryKey1 = (RegistryKey)dimension.resultOrPartial((str) -> logger.error((String) str)).orElse(World.OVERWORLD);
        } else {
            registryKey1 = World.OVERWORLD;
        }

        RegistryKey<World> registryKey = registryKey1;
        ServerWorld serverWorld = t.getServer().getWorld(registryKey);
        ServerWorld serverWorld2;
        if (serverWorld == null) {
            LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", registryKey);
            serverWorld2 = t.getServer().getOverworld();
        } else {
            serverWorld2 = serverWorld;
        }

        boolean isOverworld = DimensionTypeUtil.isOverworld(serverWorld2);
        BlockPos pos = null;
        if (isOverworld) {
            Identifier id = serverWorld2.getRegistryKey().getValue();
            pos = PlayerManagerUtil.identifierBlockPosMap.get(id);
        }


        player.setWorld(serverWorld2);
        String string2 = "local";
        if (connection.getAddress() != null) {
            string2 = connection.getAddress().toString();
        }

        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", new Object[]{player.getName().getString(), string2, player.getId(), player.getX(), player.getY(), player.getZ()});
        WorldProperties worldProperties = serverWorld2.getLevelProperties();
        player.setGameMode(nbtCompound);
        ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(t.getServer(), connection, player);
        GameRules gameRules = serverWorld2.getGameRules();
        boolean bl = gameRules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
        boolean bl2 = gameRules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
        serverPlayNetworkHandler.sendPacket(new GameJoinS2CPacket(player.getId(), worldProperties.isHardcore(), player.interactionManager.getGameMode(), player.interactionManager.getPreviousGameMode(), t.getServer().getWorldRegistryKeys(), t.getRegistryManager(), serverWorld2.method_40134(), serverWorld2.getRegistryKey(), BiomeAccess.hashSeed(serverWorld2.getSeed()), playerManager.getMaxPlayerCount(), playerManager.getViewDistance(), playerManager.getSimulationDistance(), bl2, !bl, serverWorld2.isDebugWorld(), serverWorld2.isFlat()));
        serverPlayNetworkHandler.sendPacket(new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, (new PacketByteBuf(Unpooled.buffer())).writeString(t.getServer().getServerModName())));
        serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
        serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.getAbilities()));
        serverPlayNetworkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(player.getInventory().selectedSlot));
        serverPlayNetworkHandler.sendPacket(new SynchronizeRecipesS2CPacket(t.getServer().getRecipeManager().values()));
        serverPlayNetworkHandler.sendPacket(new SynchronizeTagsS2CPacket(TagPacketSerializer.serializeTags(t.getRegistryManager())));
        playerManager.sendCommandTree(player);
        player.getStatHandler().updateStatSet();
        player.getRecipeBook().sendInitRecipesPacket(player);
        t.invokeSendScoreboard(serverWorld2.getScoreboard(), player);
        t.getServer().forcePlayerSampleUpdate();
        TranslatableText mutableText;
        if (player.getGameProfile().getName().equalsIgnoreCase(string)) {
            mutableText = new TranslatableText("multiplayer.player.joined", new Object[]{player.getDisplayName()});
        } else {
            mutableText = new TranslatableText("multiplayer.player.joined.renamed", new Object[]{player.getDisplayName(), string});
        }

        playerManager.broadcast(mutableText.formatted(Formatting.YELLOW), MessageType.SYSTEM, Util.NIL_UUID);
        serverPlayNetworkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        t.getPlayers().add(player);
        t.getPlayerMap().put(player.getUuid(), player);
        playerManager.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, new ServerPlayerEntity[]{player}));

        for(int i = 0; i < t.getPlayers().size(); ++i) {
            player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, new ServerPlayerEntity[]{(ServerPlayerEntity)t.getPlayers().get(i)}));
        }

        serverWorld2.onPlayerConnected(player);
        t.getServer().getBossBarManager().onPlayerConnect(player);
        playerManager.sendWorldInfo(player, serverWorld2);
        if (!t.getServer().getResourcePackUrl().isEmpty()) {
            player.sendResourcePackUrl(t.getServer().getResourcePackUrl(), t.getServer().getResourcePackHash(), t.getServer().requireResourcePack(), t.getServer().getResourcePackPrompt());
        }

        Iterator var24 = player.getStatusEffects().iterator();

        while(var24.hasNext()) {
            StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var24.next();
            serverPlayNetworkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), statusEffectInstance));
        }

        if (!isFirstTimeJoin && nbtCompound.contains("RootVehicle", 10)) {
            NbtCompound nbtCompound2 = nbtCompound.getCompound("RootVehicle");
            Entity entity = EntityType.loadEntityWithPassengers(nbtCompound2.getCompound("Entity"), serverWorld2, (vehicle) -> {
                return !serverWorld2.tryLoadEntity(vehicle) ? null : vehicle;
            });
            if (entity != null) {
                UUID uUID;
                if (nbtCompound2.containsUuid("Attach")) {
                    uUID = nbtCompound2.getUuid("Attach");
                } else {
                    uUID = null;
                }

                Iterator var21;
                Entity entity2;
                if (entity.getUuid().equals(uUID)) {
                    player.startRiding(entity, true);
                } else {
                    var21 = entity.getPassengersDeep().iterator();

                    while(var21.hasNext()) {
                        entity2 = (Entity)var21.next();
                        if (entity2.getUuid().equals(uUID)) {
                            player.startRiding(entity2, true);
                            break;
                        }
                    }
                }

                if (!player.hasVehicle()) {
                    LOGGER.warn("Couldn't reattach entity to player");
                    entity.discard();
                    var21 = entity.getPassengersDeep().iterator();

                    while(var21.hasNext()) {
                        entity2 = (Entity)var21.next();
                        entity2.discard();
                    }
                }
            }
        }

        player.onSpawn();

        if (isFirstTimeJoin && pos != null) {
            player.teleport(pos.getX(), pos.getY(), pos.getZ());
        }

        ci.cancel();
    }
}
