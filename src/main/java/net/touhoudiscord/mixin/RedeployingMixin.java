package net.touhoudiscord.mixin;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.touhoudiscord.HardcoreRedeploy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class RedeployingMixin {
	@Shadow public abstract boolean hasEffect(MobEffect effect);

	@Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
	private void init(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (source.is(DamageTypeTags.IS_FALL) && this.hasEffect(HardcoreRedeploy.REDEPLOYING)) cir.setReturnValue(false);
	}
}