package net.touhoudiscord.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.touhoudiscord.HardcoreRedeploy;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.RenderUtils;

public class BuyStationEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final RawAnimation OPEN = RawAnimation.begin().thenPlay("animation.model.open").thenLoop("animation.model.idle");
    private static final RawAnimation CLOSE = RawAnimation.begin().thenPlay("animation.model.close");

    public BuyStationEntity(BlockPos pos, BlockState state) {
        super(HardcoreRedeploy.BUY_STATION_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "buystationcontroller", 0, state -> {
            Vec3 pos = state.getAnimatable().getBlockPos().getCenter();
            if (state.getAnimatable().getLevel().getNearestPlayer(pos.x, pos.y, pos.z, 5, false) instanceof Player) {
                return state.setAndContinue(OPEN);
            }
            else {
                return state.setAndContinue(CLOSE);
            }
        })
                .setSoundKeyframeHandler(event -> {
                    Player player = ClientUtils.getClientPlayer();

                    if (player != null) {
                        player.level().playSound(player, this.getBlockPos(), HardcoreRedeploy.BUY_STATION_SOUND_EVENT, SoundSource.BLOCKS, 1, 1);
                    }
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
