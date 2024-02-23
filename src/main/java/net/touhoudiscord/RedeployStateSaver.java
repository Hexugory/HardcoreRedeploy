package net.touhoudiscord;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class RedeployStateSaver extends PersistentState {

    public HashMap<UUID, PlayerData> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            playerNbt.putInt("timesRevived", playerData.timesRevived);

            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static RedeployStateSaver createFromNbt(NbtCompound tag) {
        RedeployStateSaver state = new RedeployStateSaver();

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();

            playerData.timesRevived = playersNbt.getCompound(key).getInt("timesRevived");

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    public static RedeployStateSaver getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        RedeployStateSaver state = persistentStateManager.getOrCreate(RedeployStateSaver::createFromNbt, RedeployStateSaver::new, HardcoreRedeploy.MOD_ID);

        state.markDirty();

        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        RedeployStateSaver serverState = getServerState(player.getWorld().getServer());

        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());

        return playerState;
    }

    public static PlayerData getPlayerState(MinecraftServer server, UUID uuid) {
        RedeployStateSaver serverState = getServerState(server);

        PlayerData playerState = serverState.players.computeIfAbsent(uuid, i -> new PlayerData());

        return playerState;
    }
}
