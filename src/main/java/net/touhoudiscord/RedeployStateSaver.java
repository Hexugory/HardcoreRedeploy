package net.touhoudiscord;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import java.util.HashMap;
import java.util.UUID;

public class RedeployStateSaver extends SavedData {

    public HashMap<UUID, PlayerData> players = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag nbt) {

        CompoundTag playersNbt = new CompoundTag();
        players.forEach((uuid, playerData) -> {
            CompoundTag playerNbt = new CompoundTag();

            playerNbt.putInt("timesRevived", playerData.timesRevived);

            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static RedeployStateSaver createFromNbt(CompoundTag tag) {
        RedeployStateSaver state = new RedeployStateSaver();

        CompoundTag playersNbt = tag.getCompound("players");
        playersNbt.getAllKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();

            playerData.timesRevived = playersNbt.getCompound(key).getInt("timesRevived");

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    public static RedeployStateSaver getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.getLevel(Level.OVERWORLD).getDataStorage();

        RedeployStateSaver state = persistentStateManager.computeIfAbsent(RedeployStateSaver::createFromNbt, RedeployStateSaver::new, HardcoreRedeploy.MOD_ID);

        state.setDirty();

        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        RedeployStateSaver serverState = getServerState(player.level().getServer());

        PlayerData playerState = serverState.players.computeIfAbsent(player.getUUID(), uuid -> new PlayerData());

        return playerState;
    }

    public static PlayerData getPlayerState(MinecraftServer server, UUID uuid) {
        RedeployStateSaver serverState = getServerState(server);

        PlayerData playerState = serverState.players.computeIfAbsent(uuid, i -> new PlayerData());

        return playerState;
    }
}
