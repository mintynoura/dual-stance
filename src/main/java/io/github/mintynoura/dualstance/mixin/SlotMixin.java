package io.github.mintynoura.dualstance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Slot.class)
public abstract class SlotMixin {
	@Shadow
	@Final
	public Container container;

	@ModifyReturnValue(method = "mayPlace", at = @At("RETURN"))
	private boolean dualStance$preventStoringHeartSeal(boolean original, @Local(argsOnly = true, name = "itemStack") ItemStack itemStack) {
		if (!(this.container instanceof Inventory)) {
			return !itemStack.has(DualStanceComponents.LINKED_MOB);
		} else return original;
	}
}
