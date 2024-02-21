package net.touhoudiscord.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.touhoudiscord.HardcoreRedeploy;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.RenderUtils;

public class BuyStationEntity extends BlockEntity implements GeoBlockEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final RawAnimation OPEN = RawAnimation.begin().thenPlay("animation.model.open").thenLoop("animation.model.idle");
    private static final RawAnimation CLOSE = RawAnimation.begin().thenPlay("animation.model.close");

    public BuyStationEntity(BlockPos pos, BlockState state) {
        super(HardcoreRedeploy.BUY_STATION_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "buystationcontroller", 0, state -> {
            Vec3d pos = state.getAnimatable().getPos().toCenterPos();
            if (state.getAnimatable().getWorld().getClosestPlayer(pos.x, pos.y, pos.z, 5, false) instanceof PlayerEntity)
                return state.setAndContinue(OPEN);
            else
                return state.setAndContinue(CLOSE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }
}
