package net.touhoudiscord.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.touhoudiscord.BuyStationCapable;
import net.touhoudiscord.screen.BuyStationScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerEntity.class)
public class ClientBuyStationScreenMixin implements BuyStationCapable {
    @Shadow @Final protected MinecraftClient client;

    @Override
    public void hardcoreredeploy_openBuyStationScreen(BlockPos blockPos) {
        this.client.setScreen(new BuyStationScreen(blockPos));
    }
}
