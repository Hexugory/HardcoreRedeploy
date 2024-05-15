package net.touhoudiscord;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
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

	public static final StatusEffect REDEPLOYING = new RedeployingStatusEffect();
	public static final Block BUY_STATION = Registry.register(Registries.BLOCK, new Identifier(HardcoreRedeploy.MOD_ID, "buy_station"), new BuyStation(FabricBlockSettings.create().nonOpaque().requiresTool().resistance(6).hardness(3)));
	public static final Item BUY_STATION_ITEM = Registry.register(Registries.ITEM, new Identifier(HardcoreRedeploy.MOD_ID, "buy_station"), new BuyStationItem(HardcoreRedeploy.BUY_STATION, new FabricItemSettings()));
	public static final Identifier BUY_STATION_SOUND_ID = new Identifier(HardcoreRedeploy.MOD_ID, "buy_station");
	public static SoundEvent BUY_STATION_SOUND_EVENT = SoundEvent.of(BUY_STATION_SOUND_ID);
	public static BlockEntityType<BuyStationEntity> BUY_STATION_ENTITY;

	public static final Identifier SEND_REVIVES_UPDATE = new Identifier(HardcoreRedeploy.MOD_ID, "send_revives_update");
	public static final Identifier REQUEST_REVIVE = new Identifier(HardcoreRedeploy.MOD_ID, "request_revive");
	public static final Identifier SEND_REVIVE = new Identifier(HardcoreRedeploy.MOD_ID, "send_revive");
	public static final Identifier SYNC_CONFIG = new Identifier(HardcoreRedeploy.MOD_ID, "sync_config");

	private static final ItemStack firework;
	static {
		firework = new ItemStack(Registries.ITEM.get(new Identifier("minecraft", "firework_rocket")));
		NbtCompound nbt = new NbtCompound();
		nbt.putByte("Flight", (byte)3);
		firework.setSubNbt("Fireworks", nbt);
	}


	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Hardcore Redeploy");

		GeckoLib.initialize();

		ServerPlayNetworking.registerGlobalReceiver(REQUEST_REVIVE, (server, player, handler, buf, responseSender) -> {
			UUID uuid = buf.readUuid();
			BlockPos blockPos = buf.readBlockPos();

			server.execute(() -> {
				ServerPlayerEntity spectator = server.getPlayerManager().getPlayer(uuid);
				if (spectator == null) return;

				BlockState invokingBlock = player.getWorld().getBlockState(blockPos);

				if (invokingBlock.getBlock() instanceof BuyStation && player.getPos().isInRange(blockPos.toCenterPos(), 5)) {

					int cost = config.baseCost + config.additiveCost * RedeployStateSaver.getPlayerState(spectator).timesRevived;
					boolean isCreative = player.interactionManager.getGameMode() == GameMode.CREATIVE;
					if (!isCreative && player.experienceLevel < cost) return;

					Vec3d fireworkPos = blockPos.toCenterPos();
					BlockState blockState = player.getWorld().getBlockState(blockPos);
					Direction offset = blockState.get(HorizontalFacingBlock.FACING).rotateYClockwise();
					if (blockState.get(BUY_STATION_PART) == BuyStation.BuyStationPart.AUX)
						offset = offset.getOpposite();
					FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(player.getWorld(), fireworkPos.x + offset.getOffsetX() / 2., fireworkPos.y, fireworkPos.z + offset.getOffsetZ() / 2., firework);
					player.getWorld().spawnEntity(fireworkRocketEntity);

					if (!isCreative) player.setExperienceLevel(player.experienceLevel - cost);
					((TimerAccess) server).hardcoreredeploy_redeployInTicks(spectator, player, 60L);
					PacketByteBuf buf1 = PacketByteBufs.create();
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

		BUY_STATION_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(HardcoreRedeploy.MOD_ID, "buy_station_entity"),
				FabricBlockEntityTypeBuilder.create(BuyStationEntity::new,
						HardcoreRedeploy.BUY_STATION).build());
		Registry.register(Registries.STATUS_EFFECT, new Identifier(HardcoreRedeploy.MOD_ID, "redeploying"), REDEPLOYING);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> RedeployPlayerCommand.register(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SetRevivesCommand.register(dispatcher));
		Registry.register(Registries.SOUND_EVENT, BUY_STATION_SOUND_ID, BUY_STATION_SOUND_EVENT);
	}

	public static void syncConfig(MinecraftServer server, ServerPlayerEntity receiver) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(HardcoreRedeployConfigHandler.config.baseCost);
		buf.writeInt(HardcoreRedeployConfigHandler.config.additiveCost);
		server.execute(() -> {
			ServerPlayNetworking.send(receiver, SYNC_CONFIG, buf);
		});
	}

	public static void syncRevives(MinecraftServer server, ServerPlayerEntity receiver, UUID uuid) {
		PlayerData playerData = RedeployStateSaver.getPlayerState(server, uuid);
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeUuid(uuid);
		buf.writeInt(playerData.timesRevived);
		server.execute(() -> {
			ServerPlayNetworking.send(receiver, SEND_REVIVES_UPDATE, buf);
		});
	}
}