package io.github.mintynoura.dualstance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.registries.DualStanceItems;
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
			if (itemStack.has(DualStanceComponents.LINKED_PLAYER)) {
				if (itemStack.get(DualStanceComponents.LINKED_PLAYER).id() == target.getUUID()) {
					return false;
				} else {
					return original;
				}
			}
		}
		return original;
	}

	@ModifyVariable(method = "hurtServer", at = @At("HEAD"), name = "damage", argsOnly = true)
	private float dualStance$disablePacifismDamageTaken(float damage, @Local(argsOnly = true, name = "source") DamageSource source) {
		if (source.getEntity() != null) {
			for (ItemStack itemStack : this.getInventory()) {
				if (itemStack.has(DualStanceComponents.HEART_SEALED_CREST)) {
					if (itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().getItem() == DualStanceItems.PACIFISM_CREST) {
						return damage * 0;
					}
				}
			}
		}
		return damage;
	}
}
