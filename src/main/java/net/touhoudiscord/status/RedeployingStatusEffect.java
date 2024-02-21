package net.touhoudiscord.status;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class RedeployingStatusEffect extends StatusEffect {
    public RedeployingStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFFFFFF);
    }
}
