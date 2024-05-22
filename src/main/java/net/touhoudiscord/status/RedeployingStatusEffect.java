package net.touhoudiscord.status;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class RedeployingStatusEffect extends MobEffect {
    public RedeployingStatusEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFFFF);
    }
}
