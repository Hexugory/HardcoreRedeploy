package net.touhoudiscord.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.touhoudiscord.BuyStationCapable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class BuyStationScreenMixin implements BuyStationCapable {
    @Override
    public void hardcoreredeploy_openBuyStationScreen(BlockPos blockPos) {
    }
}
