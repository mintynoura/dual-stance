package io.github.mintynoura.dualstance.util;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.*;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.registries.DualStanceItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

	public static CrestComponent getHeartSealedCrestComponent(ItemStack itemStack) {
		return itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().get(DualStanceComponents.CREST);
	}
	public static ItemStack getHeartSealedCrest(ItemStack itemStack) {
		return itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest();
	}

	public static void linkPlayer(ItemStack itemStack, ItemStack otherItemStack, LivingEntity player, LivingEntity otherPlayer) {
		itemStack.set(DualStanceComponents.LINKED_MOB, new LinkedMobComponent(otherPlayer.getUUID(), otherPlayer.getScoreboardName()));
		itemStack.set(DualStanceComponents.LINKED_CREST, CrestComponent.copy(getHeartSealedCrestComponent(otherItemStack)));
		itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

		for (CrestEffect crestEffect : getHeartSealedCrestComponent(itemStack).crestEffects()) {
			if (crestEffect instanceof AttributeCrestEffect attributeCrestEffect) {
				CrestHelper.applyAttributeCrest(player, attributeCrestEffect);
			}
		}
		for (CrestEffect crestEffect2 : itemStack.get(DualStanceComponents.LINKED_CREST).crestEffects()) {
			if (crestEffect2 instanceof AttributeCrestEffect attributeCrestEffect) {
				CrestHelper.applyAttributeCrest(player, attributeCrestEffect);
			}
		}
		// pacifism attack damage attribute modifier applying
		if (getHeartSealedCrest(itemStack).getItem() == DualStanceItems.PACIFISM_CREST && !(getHeartSealedCrest(otherItemStack).getItem() == DualStanceItems.PACIFISM_CREST)) {
			CrestHelper.applyPacifismAttribute(player);
		}
	}
	public static void linkNonPlayerMob(ItemStack itemStack, LivingEntity player, LivingEntity mob) {
		itemStack.set(DualStanceComponents.LINKED_MOB, new LinkedMobComponent(mob.getUUID(), mob.getScoreboardName()));
		itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

		for (CrestEffect crestEffect : getHeartSealedCrestComponent(itemStack).crestEffects()) {
			if (crestEffect instanceof AttributeCrestEffect attributeCrestEffect) {
				CrestHelper.applyAttributeCrest(player, attributeCrestEffect);
			}
		}

		// pacifism attack damage attribute modifier applying
		if (getHeartSealedCrest(itemStack).getItem() == DualStanceItems.PACIFISM_CREST) {
			CrestHelper.applyPacifismAttribute(player);
		}
	}
	public static void unlink(ItemStack itemStack, LivingEntity entity) {
		if (getHeartSealedCrestComponent(itemStack) != null) {
			for (CrestEffect crestEffect : getHeartSealedCrestComponent(itemStack).crestEffects()) {
				if (crestEffect instanceof AttributeCrestEffect attributeCrestEffect) {
					CrestHelper.clearAttributeCrest(entity, attributeCrestEffect);
				}
			}
		}
		if (itemStack.has(DualStanceComponents.LINKED_CREST)) {
			for (CrestEffect crestEffect2 : itemStack.get(DualStanceComponents.LINKED_CREST).crestEffects()) {
				if (crestEffect2 instanceof AttributeCrestEffect attributeCrestEffect) {
					CrestHelper.clearAttributeCrest(entity, attributeCrestEffect);
				}
			}
		}
		// pacifism attack damage attribute modifier clearing
		if (getHeartSealedCrest(itemStack).getItem() == DualStanceItems.PACIFISM_CREST) {
			CrestHelper.clearPacifismAttribute(entity);
		}
		itemStack.remove(DualStanceComponents.LINKED_MOB);
		itemStack.remove(DualStanceComponents.LINKED_CREST);
		itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
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

	public static void applyPacifismAttribute(LivingEntity entity) {
		AttributeInstance instance = entity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);
		if (instance != null) {
			instance.removeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "pacifism_crest"));
			instance.addTransientModifier(new AttributeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "pacifism_crest"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		}
	}

	public static void clearPacifismAttribute(LivingEntity entity) {
		AttributeInstance instance = entity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);
		if (instance != null) {
			instance.removeModifier(Identifier.fromNamespaceAndPath(DualStance.ID, "pacifism_crest"));
		}
	}
}
