package io.github.mintynoura.dualstance.item;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.*;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.registries.DualStanceItems;
import io.github.mintynoura.dualstance.util.CrestHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class HeartSealItem extends Item {
	public HeartSealItem(Properties properties) {
		super(properties);
	}

	// Effect applying
	@Override
	public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
		super.inventoryTick(itemStack, level, owner, slot);
		if (owner.getAttached(DualStance.PAIR_OFFER_TIMER) != null && owner.getAttached(DualStance.PAIR_OFFER_TIMER) > 0) {
			owner.setAttached(DualStance.PAIR_OFFER_TIMER, owner.getAttached(DualStance.PAIR_OFFER_TIMER)-1);
		}
		if(!itemStack.has(DualStanceComponents.LINKED_PLAYER) || !(owner instanceof Player thisPlayer)) return;
		// TODO: Change back to player
		Entity otherPlayer = level.getEntity(itemStack.get(DualStanceComponents.LINKED_PLAYER).id());
		if (otherPlayer == null || otherPlayer.distanceTo(thisPlayer) > 8 || !otherPlayer.level().dimension().equals(level.dimension())){
			unlink(itemStack, owner.asLivingEntity());
		}
		if (itemStack.has(DualStanceComponents.HEART_SEALED_CREST)) {
			if (!itemStack.get(DualStanceComponents.HEART_SEALED_CREST).isEmpty() && itemStack.has(DualStanceComponents.LINKED_CREST)) {
				var effects1 = itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().get(DualStanceComponents.CREST);
				var effects2 = itemStack.get(DualStanceComponents.LINKED_CREST);
				CrestHelper.tickCrestEffect(thisPlayer, effects1);
				CrestHelper.tickCrestEffect(thisPlayer, effects2);
				CrestHelper.renderLinkParticle(thisPlayer, otherPlayer);
			} else unlink(itemStack, owner.asLivingEntity());
		}
	}

	// Linking code
	@Override
	public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand type) {
		if(!(target instanceof Player otherPlayer)) //TODO: Change back to player
			return InteractionResult.FAIL;
		ItemStack otherItemStack = otherPlayer.getItemInHand(InteractionHand.MAIN_HAND);
		if(!(otherItemStack.getItem() instanceof HeartSealItem))
			return InteractionResult.FAIL;
		if(player.getInventory().countItem(DualStanceItems.HEART_SEAL) > 1)
			return InteractionResult.FAIL; //TODO: Figure out if this is the right result of this edge case.
		if(!otherItemStack.has(DualStanceComponents.HEART_SEALED_CREST) || !itemStack.has(DualStanceComponents.HEART_SEALED_CREST)) {
			//TODO: Figure out if this is the right result of this edge case.
			return InteractionResult.FAIL;
		}
		if (itemStack.get(DualStanceComponents.HEART_SEALED_CREST).isEmpty() || otherItemStack.get(DualStanceComponents.HEART_SEALED_CREST).isEmpty()) {
			return InteractionResult.FAIL;
		}
		if (otherPlayer.getAttachedOrElse(DualStance.PAIR_OFFER_TIMER, 0) > 0) {
			link(itemStack, otherItemStack, player, otherPlayer);
			link(otherItemStack, itemStack, otherPlayer, player);
			player.setAttached(DualStance.PAIR_OFFER_TIMER, 0);
			otherPlayer.setAttached(DualStance.PAIR_OFFER_TIMER, 0);
		} else {
			player.setAttached(DualStance.PAIR_OFFER_TIMER, 200);
			player.makeSound(SoundEvents.ARROW_HIT_PLAYER);
			otherPlayer.sendOverlayMessage(Component.translatableWithFallback("overlay.dual_stance.pair_up_offer","%s wants to pair up!", player.getScoreboardName()));
		}

		player.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack self, Slot slot, ClickAction clickAction, Player player) {
		HeartSealedCrest sealedCrest = self.get(DualStanceComponents.HEART_SEALED_CREST);
		HeartSealedCrest newSealedCrest = HeartSealedCrest.EMPTY;
		if (sealedCrest == null) {
			return false;
		} else {
			ItemStack other = slot.getItem();
			if (clickAction == ClickAction.PRIMARY && !other.isEmpty()) {
				if (sealedCrest.canInsert(slot.getItem())) {
					playInsertSound(player);
					newSealedCrest = new HeartSealedCrest(other.copyWithCount(1));
					self.set(DualStanceComponents.HEART_SEALED_CREST, newSealedCrest);
					other.shrink(1);
				} else {
					playInsertFailSound(player);
				}
				this.broadcastChangesOnContainerMenu(player);
				return true;
			} else if (clickAction == ClickAction.SECONDARY && other.isEmpty() && !sealedCrest.isEmpty()) {
				ItemStack slotStack = slot.safeInsert(sealedCrest.crest());
				if (slotStack != null) {
					playRemoveOneSound(player);
				}
				self.set(DualStanceComponents.HEART_SEALED_CREST, newSealedCrest);
				this.broadcastChangesOnContainerMenu(player);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem) {
		if (clickAction == ClickAction.PRIMARY && other.isEmpty()) {
			return false;
		} else {
			HeartSealedCrest sealedCrest = self.get(DualStanceComponents.HEART_SEALED_CREST);
			HeartSealedCrest newSealedCrest = HeartSealedCrest.EMPTY;
			if (sealedCrest == null) {
				return false;
			} else {
				if (clickAction == ClickAction.PRIMARY && !other.isEmpty()) {
					if (slot.allowModification(player) && sealedCrest.canInsert(other)) {
						playInsertSound(player);
						newSealedCrest = new HeartSealedCrest(other.copyWithCount(1));
						self.set(DualStanceComponents.HEART_SEALED_CREST, newSealedCrest);
						other.shrink(1);
					} else {
						playInsertFailSound(player);
					}
					this.broadcastChangesOnContainerMenu(player);
					return true;
				} else if (clickAction == ClickAction.SECONDARY && other.isEmpty()) {
					if (slot.allowModification(player) && !sealedCrest.isEmpty()) {
						ItemStack removed = sealedCrest.crest();
						if (removed != null) {
							playRemoveOneSound(player);
							carriedItem.set(removed);
						}
					}

					self.set(DualStanceComponents.HEART_SEALED_CREST, newSealedCrest);
					this.broadcastChangesOnContainerMenu(player);
					return true;
				} else return false;
			}
		}
	}

	@Override
	public void onDestroyed(final ItemEntity entity) {
		HeartSealedCrest contents = entity.getItem().get(DualStanceComponents.HEART_SEALED_CREST);
		if (contents != null) {
			entity.getItem().set(DualStanceComponents.HEART_SEALED_CREST, HeartSealedCrest.EMPTY);
			ItemUtils.onContainerDestroyed(entity, contents.itemCopyStream());
		}
	}

	public static CrestComponent getHeartSealedCrest(ItemStack itemStack) {
		return Objects.requireNonNull(itemStack.get(DualStanceComponents.HEART_SEALED_CREST)).crest().get(DualStanceComponents.CREST);
	}

	public static void link(ItemStack itemStack, ItemStack otherItemStack, LivingEntity player, LivingEntity otherPlayer) {
		itemStack.set(DualStanceComponents.LINKED_PLAYER, new LinkedPlayerComponent(otherPlayer.getUUID(), otherPlayer.getScoreboardName()));
		itemStack.set(DualStanceComponents.LINKED_CREST, CrestComponent.copy(getHeartSealedCrest(otherItemStack)));

		for (CrestEffect crestEffect : getHeartSealedCrest(itemStack).crestEffects()) {
			if (crestEffect instanceof AttributeCrestEffect attributeCrestEffect) {
				CrestHelper.applyAttributeCrest(player, attributeCrestEffect);
			}
		}
		for (CrestEffect crestEffect2 : itemStack.get(DualStanceComponents.LINKED_CREST).crestEffects()) {
			if (crestEffect2 instanceof AttributeCrestEffect attributeCrestEffect) {
				CrestHelper.applyAttributeCrest(player, attributeCrestEffect);
			}
		}
		player.makeSound(SoundEvents.PLAYER_LEVELUP);
	}

	public static void unlink(ItemStack itemStack, LivingEntity entity) {
		for (CrestEffect crestEffect : getHeartSealedCrest(itemStack).crestEffects()) {
			if (crestEffect instanceof AttributeCrestEffect attributeCrestEffect) {
				CrestHelper.clearAttributeCrest(entity, attributeCrestEffect);
			}
		}
		if (itemStack.has(DualStanceComponents.LINKED_CREST)) {
			for (CrestEffect crestEffect2 : itemStack.get(DualStanceComponents.LINKED_CREST).crestEffects()) {
				if (crestEffect2 instanceof AttributeCrestEffect attributeCrestEffect) {
					CrestHelper.clearAttributeCrest(entity, attributeCrestEffect);
				}
			}
		}
		itemStack.remove(DualStanceComponents.LINKED_PLAYER);
		itemStack.remove(DualStanceComponents.LINKED_CREST);
		entity.makeSound(SoundEvents.VILLAGER_NO);
	}

	// TODO: add new non-bundle sounds
	private static void playRemoveOneSound(final Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertSound(final Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertFailSound(final Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(final ItemStack self) {
		TooltipDisplay display = self.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
		return !display.shows(DualStanceComponents.HEART_SEALED_CREST)
			? Optional.empty()
			: Optional.ofNullable(self.get(DualStanceComponents.HEART_SEALED_CREST)).map(HeartSealTooltip::new);
	}

	private void broadcastChangesOnContainerMenu(final Player player) {
		AbstractContainerMenu containerMenu = player.containerMenu;
		if (containerMenu != null) {
			containerMenu.slotsChanged(player.getInventory());
		}
	}
}
