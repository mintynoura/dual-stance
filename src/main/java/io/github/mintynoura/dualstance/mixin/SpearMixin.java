package io.github.mintynoura.dualstance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mintynoura.dualstance.util.CrestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.PiercingWeapon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(PiercingWeapon.class)
public abstract class SpearMixin {
	@ModifyReturnValue(method = "canHitEntity", at = @At("RETURN"))
	private static boolean dualStance$disablePacifismSpears(boolean original, @Local(argsOnly = true, name = "jabber") Entity jabber, @Local(argsOnly = true, name = "target") Entity target) {
		if (jabber instanceof Player player) {
			if (CrestHelper.isPacifismActive(player)) return false;
		}
		if (target instanceof Player player) {
			if (CrestHelper.isPacifismActive(player)) return false;
		}
		return original;
	}
}
