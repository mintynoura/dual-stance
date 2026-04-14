package io.github.mintynoura.dualstance.mixin;

import io.github.mintynoura.dualstance.client.ClientHeartSealTooltip;
import io.github.mintynoura.dualstance.item.component.HeartSealTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipComponentMixin {
	@Inject(method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;",
	at = @At("HEAD"), cancellable = true)
	private static void dualStance$addClientTooltip(TooltipComponent component, CallbackInfoReturnable<ClientTooltipComponent> cir) {
		if (component instanceof HeartSealTooltip heartSealTooltip) {
			cir.setReturnValue(new ClientHeartSealTooltip(heartSealTooltip.contents()));
		}
	}
}
