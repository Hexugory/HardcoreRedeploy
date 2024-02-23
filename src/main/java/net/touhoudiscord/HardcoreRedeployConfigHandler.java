package net.touhoudiscord;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class HardcoreRedeployConfigHandler {
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("hardcore_redeploy.json");
    private static final Gson gson = new Gson();
    public static HardcoreRedeployConfig config = readOrCreateConfig();

    public HardcoreRedeployConfigHandler() {
    }

    private static HardcoreRedeployConfig readOrCreateConfig() {
        try {
            return gson.fromJson(Files.readString(configPath), HardcoreRedeployConfig.class);
        }
        catch(IOException e) {
            HardcoreRedeployConfig newConfig = new HardcoreRedeployConfig();
            try {
                Files.writeString(configPath, gson.toJson(newConfig), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return newConfig;
        }
    }

    public static class HardcoreRedeployConfig {
        public int baseCost = 10;
        public int additiveCost = 10;
        HardcoreRedeployConfig(){}
    }
}
