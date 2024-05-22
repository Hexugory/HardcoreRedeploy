package net.touhoudiscord.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.touhoudiscord.BuyStationCapable;
import net.touhoudiscord.screen.BuyStationScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public class ClientBuyStationScreenMixin implements BuyStationCapable {
    @Shadow @Final protected Minecraft minecraft;

    @Override
    public void hardcoreredeploy_openBuyStationScreen(BlockPos blockPos) {
        this.minecraft.setScreen(new BuyStationScreen(blockPos));
    }
}
