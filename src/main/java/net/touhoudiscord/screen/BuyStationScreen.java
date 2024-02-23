package net.touhoudiscord.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
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
        super(Text.literal("Buy Station"));
        this.blockPos = blockPos;
    }

    @Override
    protected void init() {
        super.init();
        playerList = new PlayerListWidget(client, width+38, height, height/2-58, height/2+84, 28);
        client.player.networkHandler.getListedPlayerListEntries().forEach(entry -> {
            if (entry.getGameMode() == GameMode.SPECTATOR) playerList.addPlayerEntry(new PlayerListEntry(Text.literal(entry.getProfile().getName()), entry.getProfile().getId()));
        });

        addDrawableChild(playerList);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.fill(width/2-93, height/2-84, width/2+93, height/2+84, 0x8F_000000);
        context.fill(width/2-93, height/2-84, width/2+93, height/2-58, 0xA0_000000);
        context.getMatrices().push();
        context.getMatrices().scale(1.5f, 1.5f, 1f);
        context.drawText(textRenderer, Text.literal("Buy Station"), Math.round((width/2-80)/1.5f), Math.round((height/2-75)/1.5f), 0xFF_73c0e7, false);
        context.getMatrices().pop();
        Text money = Text.literal("$").append(Text.literal(String.valueOf(client.player.experienceLevel*100)));
        context.drawText(textRenderer, money, width/2+80-textRenderer.getWidth(money), height/2-72, 0xFF_FFFFFF, false);
        playerList.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public class PlayerListWidget extends ElementListWidget<PlayerListEntry> {
        public PlayerListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
            super(minecraftClient, i, j, k, l, m);
            this.setRenderBackground(false);
            this.setRenderHorizontalShadows(false);
        }

        public void addPlayerEntry(PlayerListEntry entry) {
            this.addEntry(entry);
        }
    }

    public class PlayerListEntry extends ElementListWidget.Entry<PlayerListEntry> {
        private final ButtonWidget button;
        private final Text name;
        private final UUID uuid;

        public PlayerListEntry(Text name, UUID uuid) {
            this.button = ButtonWidget.builder(name, button1 -> {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeUuid(uuid);
                        buf.writeBlockPos(blockPos);
                        ClientPlayNetworking.send(HardcoreRedeploy.REQUEST_REVIVE, buf);
                        client.setScreen(null);
                    })
                    .position(4,2)
                    .size(178, 18)
                    .build();
            this.name = name;
            this.uuid = uuid;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(button);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(button);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            button.setX(x);
            button.setY(y);
            boolean isHovered = mouseX >= x && mouseX <= x+178 && mouseY >= y && mouseY <= y+18;
            Integer revives = HardcoreRedeployClient.reviveMap.get(this.uuid);
            int cost = serverConfig.baseCost+(revives == null ? 0 : revives)*serverConfig.additiveCost;
            int backgroundColor = ((isHovered ? 0x30 : 0x20) << 24) +
                    ((client.player.experienceLevel >= cost) ? 0x2397d1 : 0xa3a3a3);
            context.fill(x, y, x+178, y+18, backgroundColor);
            context.drawText(textRenderer, button.getMessage(), x+4, y+5, 0xFF_73c0e7, false);
            Text money = Text.literal("$").append(Text.literal(String.valueOf(cost*100)));
            context.drawText(textRenderer, money, x+178-4-textRenderer.getWidth(money), y+5, 0xFF_7efc20, false);
        }
    }
}
