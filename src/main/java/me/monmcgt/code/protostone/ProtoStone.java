package me.monmcgt.code.protostone;

import me.monmcgt.code.protostone.util.PlayerManagerUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.world.World;

public class ProtoStone implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            // check whether the player has spawn point set or not
            boolean hasSpawnPoint = newPlayer.getSpawnPointPosition() != null;
//            System.out.println("hasSpawnPoint = " + hasSpawnPoint);
            if (!hasSpawnPoint) {
                // teleport the player to the spawn point
                World world = newPlayer.getWorld();
                int x = world.getLevelProperties().getSpawnX();
                int y = world.getLevelProperties().getSpawnY();
                int z = world.getLevelProperties().getSpawnZ();
                y = PlayerManagerUtil.getShouldSpawnHeight(x, y, z, world);
                newPlayer.teleport(x, y, z);
//                System.out.println("Teleported player to x: " + x + " y: " + y + " z: " + z);
            }
        });

        Var.LOGGER.info("[ProtoStone] The mod has been initialised.");
    }
}
