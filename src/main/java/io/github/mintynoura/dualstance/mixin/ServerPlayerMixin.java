package io.github.mintynoura.dualstance.mixin;

import com.mojang.authlib.GameProfile;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.util.CrestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
	@Shadow
	public abstract ServerLevel level();

	public ServerPlayerMixin(Level level, GameProfile gameProfile) {
		super(level, gameProfile);
	}

	@Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"))
	private void dualStance$unlinkDroppedSeal(ItemStack itemStack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
		if (itemStack.has(DualStanceComponents.LINKED_MOB)) {
			if (itemStack.has(DualStanceComponents.LINKED_CREST)) {
				Entity linkedMob = this.level().getEntity(itemStack.get(DualStanceComponents.LINKED_MOB).id());
				if (linkedMob instanceof ServerPlayer otherPlayer) {
					for (ItemStack otherItemStack : otherPlayer.getInventory()) {
						if (otherItemStack.has(DualStanceComponents.LINKED_MOB)) {
							if (otherItemStack.get(DualStanceComponents.LINKED_MOB).id().equals(this.getUUID())) {
								CrestHelper.unlink(otherItemStack, otherPlayer);
							}
						}
					}
				}
			}
			CrestHelper.unlink(itemStack, this);
		}
	}
}
