package io.github.mintynoura.dualstance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mintynoura.dualstance.item.component.CrestEffect;
import io.github.mintynoura.dualstance.item.component.DamageNegationCrestEffect;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.registries.DualStanceItems;
import io.github.mintynoura.dualstance.registries.DualStanceSoundEvents;
import io.github.mintynoura.dualstance.util.CrestHelper;
import io.github.mintynoura.dualstance.util.CrestIdentifiers;
import io.github.mintynoura.dualstance.util.DualStanceTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public abstract class PlayerMixin extends Avatar implements ContainerUser {
	@Shadow
	public abstract Inventory getInventory();

	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}

	@ModifyReturnValue(method = "canHarmPlayer", at = @At("RETURN"))
	private boolean dualStance$disableHarmingPartner(boolean original, @Local(argsOnly = true, name = "target") Player target) {
		for (ItemStack itemStack : this.getInventory()) {
			if (itemStack.has(DualStanceComponents.LINKED_MOB)) {
				if (itemStack.get(DualStanceComponents.LINKED_MOB).id() == target.getUUID()) {
					return false;
				} else {
					return original;
				}
			}
		}
		return original;
	}

	@ModifyVariable(method = "hurtServer", at = @At("HEAD"), name = "damage", argsOnly = true)
	private float dualStance$modifyCrestDamageTaken(float damage, @Local(argsOnly = true, name = "source") DamageSource source) {
		if (source.getEntity() != null && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			for (ItemStack itemStack : this.getInventory()) {
				if (itemStack.has(DualStanceComponents.HEART_SEALED_CREST) && itemStack.has(DualStanceComponents.LINKED_MOB)) {
					// damage negation
					for (CrestEffect crestEffect : CrestHelper.collectCrestEffects(itemStack)) {
						if (crestEffect instanceof DamageNegationCrestEffect(
							float baseChance, boolean doProximityBoost)) {
							float chance = baseChance;
							if (doProximityBoost) {
								Entity linkedEntity = this.level().getEntity(itemStack.get(DualStanceComponents.LINKED_MOB).id());
								if (this.distanceTo(linkedEntity) < 4f) {
									chance += baseChance * (float) Math.sqrt((4f - this.distanceTo(linkedEntity)) / 4f);
								}
							}
							if (this.getRandom().nextFloat() <= chance) {
								damage = 0;
								this.level().playSound(null, this.getOnPos(), DualStanceSoundEvents.DAMAGE_NEGATION, SoundSource.PLAYERS);
								return damage;
							}
						}
					}
					// pacifism damage taken is disabled
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
					// enchanter damage taken is increased
					else if (itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().getItem() == DualStanceItems.ENCHANTER_CREST) {
						if (itemStack.has(DualStanceComponents.LINKED_CREST)) {
							if (!itemStack.get(DualStanceComponents.LINKED_CREST).id().equals(CrestIdentifiers.ENCHANTER_CREST)) {
								if (!source.is(DualStanceTags.DamageTypes.CREST_INCREASE_EXEMPT))
									damage = damage * 3f + 2f;
							}
						} else {
							if (!source.is(DualStanceTags.DamageTypes.CREST_INCREASE_EXEMPT))
								damage = damage * 3f + 2f;
						}
					}
					// hatred damage taken is increased
					else if (itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().getItem() == DualStanceItems.HATRED_CREST) {
						if (!source.is(DualStanceTags.DamageTypes.CREST_INCREASE_EXEMPT))
							damage = damage * 4f + 5f;
					}
					return damage;
				}
			}
		}
		return damage;
	}
}
