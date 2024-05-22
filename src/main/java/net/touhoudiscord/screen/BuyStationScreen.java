package net.touhoudiscord.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import net.touhoudiscord.HardcoreRedeploy;
import net.touhoudiscord.HardcoreRedeployClient;

import java.util.List;
import java.util.UUID;

import static net.touhoudiscord.HardcoreRedeployClient.serverConfig;

@Environment(EnvType.CLIENT)
public class BuyStationScreen extends Screen {
    private final BlockPos blockPos;

    private PlayerListWidget playerList;

    public BuyStationScreen(BlockPos blockPos) {
        super(Component.literal("Buy Station"));
        this.blockPos = blockPos;
    }

    @Override
    protected void init() {
        super.init();
        playerList = new PlayerListWidget(minecraft, width+38, height, height/2-58, height/2+84, 28);
        minecraft.player.connection.getListedOnlinePlayers().forEach(entry -> {
            if (entry.getGameMode() == GameType.SPECTATOR) playerList.addPlayerEntry(new PlayerListEntry(Component.literal(entry.getProfile().getName()), entry.getProfile().getId()));
        });

        addRenderableWidget(playerList);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.fill(width/2-93, height/2-84, width/2+93, height/2+84, 0x8F_000000);
        context.fill(width/2-93, height/2-84, width/2+93, height/2-58, 0xA0_000000);
        context.pose().pushPose();
        context.pose().scale(1.5f, 1.5f, 1f);
        context.drawString(font, Component.literal("Buy Station"), Math.round((width/2-80)/1.5f), Math.round((height/2-75)/1.5f), 0xFF_73c0e7, false);
        context.pose().popPose();
        Component money = Component.literal("$").append(Component.literal(String.valueOf(minecraft.player.experienceLevel*100)));
        context.drawString(font, money, width/2+80-font.width(money), height/2-72, 0xFF_FFFFFF, false);
        playerList.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public class PlayerListWidget extends ContainerObjectSelectionList<PlayerListEntry> {
        public PlayerListWidget(Minecraft minecraftClient, int i, int j, int k, int l, int m) {
            super(minecraftClient, i, j, k, l, m);
            this.setRenderBackground(false);
            this.setRenderTopAndBottom(false);
        }

        public void addPlayerEntry(PlayerListEntry entry) {
            this.addEntry(entry);
        }
    }

    public class PlayerListEntry extends ContainerObjectSelectionList.Entry<PlayerListEntry> {
        private final Button button;
        private final Component name;
        private final UUID uuid;

        public PlayerListEntry(Component name, UUID uuid) {
            this.button = Button.builder(name, button1 -> {
                        FriendlyByteBuf buf = PacketByteBufs.create();
                        buf.writeUUID(uuid);
                        buf.writeBlockPos(blockPos);
                        ClientPlayNetworking.send(HardcoreRedeploy.REQUEST_REVIVE, buf);
                        minecraft.setScreen(null);
                    })
                    .pos(4,2)
                    .size(178, 18)
                    .build();
            this.name = name;
            this.uuid = uuid;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(button);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(button);
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            button.setX(x);
            button.setY(y);
            boolean isHovered = mouseX >= x && mouseX <= x+178 && mouseY >= y && mouseY <= y+18;
            Integer revives = HardcoreRedeployClient.reviveMap.get(this.uuid);
            int cost = serverConfig.baseCost+(revives == null ? 0 : revives)*serverConfig.additiveCost;
            int backgroundColor = ((isHovered ? 0x30 : 0x20) << 24) +
                    ((minecraft.player.experienceLevel >= cost) ? 0x2397d1 : 0xa3a3a3);
            context.fill(x, y, x+178, y+18, backgroundColor);
            context.drawString(font, button.getMessage(), x+4, y+5, 0xFF_73c0e7, false);
            Component money = Component.literal("$").append(Component.literal(String.valueOf(cost*100)));
            context.drawString(font, money, x+178-4-font.width(money), y+5, 0xFF_7efc20, false);
        }
    }
}
