package io.github.mintynoura.dualstance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.registries.DualStanceItems;
import net.minecraft.resources.Identifier;
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
							if (!itemStack.get(DualStanceComponents.LINKED_CREST).id().equals(Identifier.fromNamespaceAndPath(DualStance.ID, "pacifism_crest"))) {
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
	private float dualStance$disablePacifismAttackDamage(float damage, @Local(argsOnly = true, name = "source") DamageSource source) {
		if (source.getEntity() != null && source.getEntity() instanceof Player player && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			for (ItemStack itemStack : player.getInventory()) {
				if (itemStack.has(DualStanceComponents.HEART_SEALED_CREST) && itemStack.has(DualStanceComponents.LINKED_MOB)) {
					if (itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().getItem() == DualStanceItems.PACIFISM_CREST) {
						if (itemStack.has(DualStanceComponents.LINKED_CREST)) {
							if (!itemStack.get(DualStanceComponents.LINKED_CREST).id().equals(Identifier.fromNamespaceAndPath(DualStance.ID, "pacifism_crest"))) {
								damage = 0;
								return damage;
							}
						} else {
							damage = 0;
							return damage;
						}
					}
				}
			}
		}
		return damage;
	}
}
