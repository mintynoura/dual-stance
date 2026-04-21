package io.github.mintynoura.dualstance.util;

import io.github.mintynoura.dualstance.item.component.AttributeCrestEffect;
import io.github.mintynoura.dualstance.item.component.CrestComponent;
import io.github.mintynoura.dualstance.item.component.CrestEffect;
import io.github.mintynoura.dualstance.item.component.MobEffectCrestEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class CrestHelper {

	public static void renderLinkParticle(Player p1, Entity p2){
		Vec3 pos1 = p1.position();
		Vec3 pos2 = p2.position();
		if(!(p1.level() instanceof ServerLevel lvl))
			return;
		if(lvl.getRandom().nextFloat() < 0.9)
			return;
		int nodeCount = 10;
		for (int i = 0; i < nodeCount; i++) {
			float x = lerp((float) pos1.x, (float) pos2.x, i*1f/nodeCount);
			float y = lerp((float) pos1.y, (float) pos2.y, i*1f/nodeCount);
			float z = lerp((float) pos1.z, (float) pos2.z, i*1f/nodeCount);
			lvl.sendParticles(ParticleTypes.CHERRY_LEAVES, x, y+1, z, 1,0.1, 0, 0.1, 0);
		}
	}
	public static float lerp(float x1, float x2, float t){
		return x1+ t*(x2-x1);
	}

	public static void tickCrestEffect(LivingEntity entity, CrestComponent effects) {
		for (CrestEffect crestEffect : effects.crestEffects()) {
			if (crestEffect instanceof MobEffectCrestEffect mobEffectCrestEffect) {
				loopMobEffectCrest(entity, mobEffectCrestEffect);
			}
		}
	}

	public static void loopMobEffectCrest(LivingEntity entity, MobEffectCrestEffect crestEffect) {
		if (entity.level().getGameTime() % crestEffect.interval() == 0L) {
			crestEffect.trigger(entity.level(), entity);
		}
	}

	public static void applyAttributeCrest(LivingEntity entity, AttributeCrestEffect crestEffect) {
		for (AttributeCrestEffect.Entry modifierEntry : crestEffect.modifiers()) {
			AttributeInstance instance = entity.getAttributes().getInstance(modifierEntry.attribute());
			if (instance != null) {
				instance.removeModifier(modifierEntry.modifier().id());
				instance.addTransientModifier(modifierEntry.modifier());
			}
		}
	}

	public static void clearAttributeCrest(LivingEntity entity, AttributeCrestEffect crestEffect) {
		for (AttributeCrestEffect.Entry modifierEntry : crestEffect.modifiers()) {
			AttributeInstance instance = entity.getAttributes().getInstance(modifierEntry.attribute());
			if (instance != null) {
				instance.removeModifier(modifierEntry.modifier().id());
			}
		}
	}
}
