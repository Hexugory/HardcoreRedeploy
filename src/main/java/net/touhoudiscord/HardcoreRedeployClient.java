package net.touhoudiscord;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.touhoudiscord.HardcoreRedeployConfigHandler.HardcoreRedeployConfig;
import net.touhoudiscord.block.client.BuyStationRenderer;
import net.touhoudiscord.screen.RedeployingScreen;

import java.util.HashMap;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class HardcoreRedeployClient implements ClientModInitializer {

	public static HashMap<UUID, Integer> reviveMap = new HashMap<>();
	public static HardcoreRedeployConfig serverConfig = HardcoreRedeployConfigHandler.config;

	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(HardcoreRedeploy.BUY_STATION_ENTITY, BuyStationRenderer::new);

		ClientPlayNetworking.registerGlobalReceiver(HardcoreRedeploy.SEND_REVIVES_UPDATE, (client, handler, buf, responseSender) -> {
			UUID uuid = buf.readUUID();
			int revives = buf.readInt();
			client.execute(() -> {
				reviveMap.put(uuid, revives);

				HardcoreRedeploy.LOGGER.info("Synced player revives");
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(HardcoreRedeploy.SEND_REVIVE, (client, handler, buf, responseSender) -> {
			client.execute(() -> {
				client.setScreen(new RedeployingScreen(6*20));
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(HardcoreRedeploy.SYNC_CONFIG, (client, handler, buf, responseSender) -> {
			serverConfig = new HardcoreRedeployConfig();
			serverConfig.baseCost = buf.readInt();
			serverConfig.additiveCost = buf.readInt();

			HardcoreRedeploy.LOGGER.info("Synced server config");
		});
	}
}