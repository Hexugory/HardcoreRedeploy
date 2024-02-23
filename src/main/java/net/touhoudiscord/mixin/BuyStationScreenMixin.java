package net.touhoudiscord.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.touhoudiscord.BuyStationCapable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public class BuyStationScreenMixin implements BuyStationCapable {
    @Override
    public void hardcoreredeploy_openBuyStationScreen(BlockPos blockPos) {
    }
}
