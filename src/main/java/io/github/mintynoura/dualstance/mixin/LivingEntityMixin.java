package io.github.mintynoura.dualstance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mintynoura.dualstance.item.component.crest_effects.CrestEffect;
import io.github.mintynoura.dualstance.item.component.crest_effects.DamageBoostCrestEffect;
import io.github.mintynoura.dualstance.item.component.crest_effects.FoodLeechCrestEffect;
import io.github.mintynoura.dualstance.item.component.crest_effects.HealthLeechCrestEffect;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.registries.DualStanceItems;
import io.github.mintynoura.dualstance.registries.DualStanceSoundEvents;
import io.github.mintynoura.dualstance.util.CrestHelper;
import io.github.mintynoura.dualstance.util.CrestIdentifiers;
import io.github.mintynoura.dualstance.util.DualStanceTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@ModifyReturnValue(method = "canBeSeenAsEnemy", at = @At("RETURN"))
	private boolean dualStance$disablePacifistTargeting(boolean original) {
		if (((LivingEntity)(Object) this) instanceof Player player) {
			for (ItemStack itemStack : player.getInventory()) {
				if (itemStack.has(DualStanceComponents.HEART_SEALED_CREST) && itemStack.has(DualStanceComponents.LINKED_MOB)) {
					if (itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().getItem() == DualStanceItems.PACIFISM_CREST) {
						if (itemStack.has(DualStanceComponents.LINKED_CREST)) {
							if (!itemStack.get(DualStanceComponents.LINKED_CREST).id().equals(CrestIdentifiers.PACIFISM_CREST)) {
								return false;
							}
						} else return false;
					}
				}
			}
		}
		return original;
	}

	@ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true, name = "damage")
	private float dualStance$modifyCrestDamageDealt(float damage, @Local(argsOnly = true, name = "source") DamageSource source) {
		if (source.getEntity() != null && source.getEntity() instanceof Player player && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			for (ItemStack itemStack : player.getInventory()) {
				if (itemStack.has(DualStanceComponents.HEART_SEALED_CREST) && itemStack.has(DualStanceComponents.LINKED_MOB)) {
					// disables damage dealt for the pacifism crest
					if (itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().getItem() == DualStanceItems.PACIFISM_CREST) {
						if (itemStack.has(DualStanceComponents.LINKED_CREST)) {
							if (!itemStack.get(DualStanceComponents.LINKED_CREST).id().equals(CrestIdentifiers.PACIFISM_CREST)) {
								damage = 0;
								return damage;

							}
						} else {
							damage = 0;
							return damage;
						}
					}
					// halves damage dealt for the enchanter crest
					else if (itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().getItem() == DualStanceItems.ENCHANTER_CREST) {
						if (itemStack.has(DualStanceComponents.LINKED_CREST)) {
							if (!itemStack.get(DualStanceComponents.LINKED_CREST).id().equals(CrestIdentifiers.ENCHANTER_CREST)) {
								damage *= 0.5f;
							}
						} else {
							damage *= 0.5f;
						}
					}
					// increase damage dealt for the hatred crest
					else if (itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().getItem() == DualStanceItems.HATRED_CREST) {
						if (!source.is(DualStanceTags.DamageTypes.CREST_INCREASE_EXEMPT))
							damage *= 2f;
					}
					// damage boosts and effects
					for (CrestEffect crestEffect : CrestHelper.collectCrestEffects(itemStack)) {
						if (crestEffect instanceof DamageBoostCrestEffect(
							float baseChance, boolean doProximityBoost, DamageBoostCrestEffect.Modifier modifier
						)) {
							float chance = baseChance;
							if (doProximityBoost) {
								Entity linkedEntity = player.level().getEntity(itemStack.get(DualStanceComponents.LINKED_MOB).id());
								if (player.distanceTo(linkedEntity) < 4f) {
									chance += baseChance * (float) Math.sqrt((4f - player.distanceTo(linkedEntity)) / 4f);
								}
							}
							if (player.getRandom().nextFloat() <= chance) {
								if (modifier.multiply()) {
									damage *= modifier.amount();
								} else damage += modifier.amount();
								player.level().playSound(null, player.getOnPos(), DualStanceSoundEvents.DAMAGE_BOOST, SoundSource.PLAYERS);
							}
						}
						if (crestEffect instanceof HealthLeechCrestEffect(float amount, boolean fractional)) {
							if (fractional) {
								player.heal(amount * damage);
							} else if (damage > 0) player.heal(amount);
						}
						if (crestEffect instanceof FoodLeechCrestEffect(int food, float saturationModifier)) {
							player.getFoodData().eat(food, saturationModifier);
						}
					}
					return damage;
				}
			}
		}
		return damage;
	}
}
