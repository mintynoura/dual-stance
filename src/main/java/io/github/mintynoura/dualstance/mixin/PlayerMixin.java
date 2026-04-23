package io.github.mintynoura.dualstance.mixin;

import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends Avatar implements ContainerUser {
	@Shadow
	public abstract Inventory getInventory();

	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}

	@Inject(method = "canHarmPlayer", at = @At("HEAD"), cancellable = true)
	private void dualStance$disableHarmingPartner(Player target, CallbackInfoReturnable<Boolean> cir) {
		for (ItemStack itemStack : this.getInventory()) {
			if (itemStack.has(DualStanceComponents.LINKED_PLAYER)) {
				if (itemStack.get(DualStanceComponents.LINKED_PLAYER).id() == target.getUUID()) {
					cir.setReturnValue(false);
				} else {
					cir.cancel();
				}
			}
		}
	}
}
