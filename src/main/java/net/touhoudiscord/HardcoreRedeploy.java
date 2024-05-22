package net.touhoudiscord;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.touhoudiscord.block.BuyStation;
import net.touhoudiscord.block.BuyStationEntity;
import net.touhoudiscord.commands.RedeployPlayerCommand;
import net.touhoudiscord.commands.SetRevivesCommand;
import net.touhoudiscord.item.BuyStationItem;
import net.touhoudiscord.status.RedeployingStatusEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

import java.util.UUID;

import static net.touhoudiscord.HardcoreRedeployConfigHandler.config;
import static net.touhoudiscord.block.BuyStation.BUY_STATION_PART;

public class HardcoreRedeploy implements ModInitializer {
	public static final String MOD_ID = "hardcore_redeploy";
    public static final Logger LOGGER = LoggerFactory.getLogger("hardcore_redeploy");

	public static final MobEffect REDEPLOYING = new RedeployingStatusEffect();
	public static final Block BUY_STATION = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(HardcoreRedeploy.MOD_ID, "buy_station"), new BuyStation(FabricBlockSettings.of().noOcclusion().requiresCorrectToolForDrops().explosionResistance(6).destroyTime(3)));
	public static final Item BUY_STATION_ITEM = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(HardcoreRedeploy.MOD_ID, "buy_station"), new BuyStationItem(HardcoreRedeploy.BUY_STATION, new FabricItemSettings()));
	public static final ResourceLocation BUY_STATION_SOUND_ID = new ResourceLocation(HardcoreRedeploy.MOD_ID, "buy_station");
	public static SoundEvent BUY_STATION_SOUND_EVENT = SoundEvent.createVariableRangeEvent(BUY_STATION_SOUND_ID);
	public static BlockEntityType<BuyStationEntity> BUY_STATION_ENTITY;

	public static final ResourceLocation SEND_REVIVES_UPDATE = new ResourceLocation(HardcoreRedeploy.MOD_ID, "send_revives_update");
	public static final ResourceLocation REQUEST_REVIVE = new ResourceLocation(HardcoreRedeploy.MOD_ID, "request_revive");
	public static final ResourceLocation SEND_REVIVE = new ResourceLocation(HardcoreRedeploy.MOD_ID, "send_revive");
	public static final ResourceLocation SYNC_CONFIG = new ResourceLocation(HardcoreRedeploy.MOD_ID, "sync_config");

	private static final ItemStack firework;
	static {
		firework = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft", "firework_rocket")));
		CompoundTag nbt = new CompoundTag();
		nbt.putByte("Flight", (byte)3);
		firework.addTagElement("Fireworks", nbt);
	}


	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Hardcore Redeploy");

		GeckoLib.initialize();

		ServerPlayNetworking.registerGlobalReceiver(REQUEST_REVIVE, (server, player, handler, buf, responseSender) -> {
			UUID uuid = buf.readUUID();
			BlockPos blockPos = buf.readBlockPos();

			server.execute(() -> {
				ServerPlayer spectator = server.getPlayerList().getPlayer(uuid);
				if (spectator == null) return;

				BlockState invokingBlock = player.level().getBlockState(blockPos);

				if (invokingBlock.getBlock() instanceof BuyStation && player.position().closerThan(blockPos.getCenter(), 5)) {

					int cost = config.baseCost + config.additiveCost * RedeployStateSaver.getPlayerState(spectator).timesRevived;
					boolean isCreative = player.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
					if (!isCreative && player.experienceLevel < cost) return;

					Vec3 fireworkPos = blockPos.getCenter();
					BlockState blockState = player.level().getBlockState(blockPos);
					Direction offset = blockState.getValue(HorizontalDirectionalBlock.FACING).getClockWise();
					if (blockState.getValue(BUY_STATION_PART) == BuyStation.BuyStationPart.AUX)
						offset = offset.getOpposite();
					FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(player.level(), fireworkPos.x + offset.getStepX() / 2., fireworkPos.y, fireworkPos.z + offset.getStepZ() / 2., firework);
					player.level().addFreshEntity(fireworkRocketEntity);

					if (!isCreative) player.setExperienceLevels(player.experienceLevel - cost);
					((TimerAccess) server).hardcoreredeploy_redeployInTicks(spectator, player, 60L);
					FriendlyByteBuf buf1 = PacketByteBufs.create();
					ServerPlayNetworking.send(spectator, SEND_REVIVE, buf1);
				}
			});
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			syncConfig(server, handler.getPlayer());

			RedeployStateSaver.getServerState(server).players.forEach((uuid, playerData) -> {
				syncRevives(server, handler.getPlayer(), uuid);
			});
		});

		BUY_STATION_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
				new ResourceLocation(HardcoreRedeploy.MOD_ID, "buy_station_entity"),
				FabricBlockEntityTypeBuilder.create(BuyStationEntity::new,
						HardcoreRedeploy.BUY_STATION).build());
		Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(HardcoreRedeploy.MOD_ID, "redeploying"), REDEPLOYING);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> RedeployPlayerCommand.register(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SetRevivesCommand.register(dispatcher));
		Registry.register(BuiltInRegistries.SOUND_EVENT, BUY_STATION_SOUND_ID, BUY_STATION_SOUND_EVENT);
	}

	public static void syncConfig(MinecraftServer server, ServerPlayer receiver) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeInt(HardcoreRedeployConfigHandler.config.baseCost);
		buf.writeInt(HardcoreRedeployConfigHandler.config.additiveCost);
		server.execute(() -> {
			ServerPlayNetworking.send(receiver, SYNC_CONFIG, buf);
		});
	}

	public static void syncRevives(MinecraftServer server, ServerPlayer receiver, UUID uuid) {
		PlayerData playerData = RedeployStateSaver.getPlayerState(server, uuid);
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeUUID(uuid);
		buf.writeInt(playerData.timesRevived);
		server.execute(() -> {
			ServerPlayNetworking.send(receiver, SEND_REVIVES_UPDATE, buf);
		});
	}
}