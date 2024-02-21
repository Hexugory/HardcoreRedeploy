package net.touhoudiscord;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.touhoudiscord.block.BuyStation;
import net.touhoudiscord.block.BuyStationEntity;
import net.touhoudiscord.commands.RedeployPlayerCommand;
import net.touhoudiscord.item.BuyStationItem;
import net.touhoudiscord.status.RedeployingStatusEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class HardcoreRedeploy implements ModInitializer {
	public static final String MOD_ID = "hardcore-redeploy";
    public static final Logger LOGGER = LoggerFactory.getLogger("hardcore-redeploy");

	public static final StatusEffect REDEPLOYING = new RedeployingStatusEffect();
	public static final Block BUY_STATION = Registry.register(Registries.BLOCK, new Identifier(HardcoreRedeploy.MOD_ID, "buy_station"), new BuyStation(FabricBlockSettings.copyOf(Blocks.HOPPER).nonOpaque()));
	public static final Item BUY_STATION_ITEM = Registry.register(Registries.ITEM, new Identifier(HardcoreRedeploy.MOD_ID, "buy_station"), new BuyStationItem(HardcoreRedeploy.BUY_STATION, new FabricItemSettings()));
	public static BlockEntityType<BuyStationEntity> BUY_STATION_ENTITY;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Hardcore Redeploy");

		GeckoLib.initialize();

		BUY_STATION_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(HardcoreRedeploy.MOD_ID, "buy_station_entity"),
				FabricBlockEntityTypeBuilder.create(BuyStationEntity::new,
						HardcoreRedeploy.BUY_STATION).build());
		Registry.register(Registries.STATUS_EFFECT, new Identifier(HardcoreRedeploy.MOD_ID, "redeploying"), REDEPLOYING);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> RedeployPlayerCommand.register(dispatcher));
	}
}