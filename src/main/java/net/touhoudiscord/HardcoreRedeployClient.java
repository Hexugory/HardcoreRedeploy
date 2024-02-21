package net.touhoudiscord;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.touhoudiscord.block.client.BuyStationRenderer;

public class HardcoreRedeployClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(HardcoreRedeploy.BUY_STATION_ENTITY, BuyStationRenderer::new);
	}
}