package net.touhoudiscord.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.tag.DamageTypeTags;
import net.touhoudiscord.HardcoreRedeploy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class RedeployingMixin {
	@Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

	@Inject(at = @At("HEAD"), method = "damage", cancellable = true)
	private void init(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (source.isIn(DamageTypeTags.IS_FALL) && this.hasStatusEffect(HardcoreRedeploy.REDEPLOYING)) cir.setReturnValue(false);
	}
}