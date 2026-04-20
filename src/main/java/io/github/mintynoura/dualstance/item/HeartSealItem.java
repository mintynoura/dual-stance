package io.github.mintynoura.dualstance.item;

import io.github.mintynoura.dualstance.item.component.HeartSealedCrest;
import io.github.mintynoura.dualstance.item.component.HeartSealTooltip;
import io.github.mintynoura.dualstance.item.component.LinkedPlayerComponent;
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

import java.util.Optional;

public class HeartSealItem extends Item {
	public HeartSealItem(Properties properties) {
		super(properties);
	}

	// Effect applying
	@Override
	public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
		super.inventoryTick(itemStack, level, owner, slot);
		if(!itemStack.has(DualStanceComponents.LINKED_PLAYER) || !(owner instanceof Player thisPlayer)) return;
		// TODO: Change back to player
		Entity otherPlayer = level.getEntity(itemStack.get(DualStanceComponents.LINKED_PLAYER).id());
		if (otherPlayer == null || otherPlayer.distanceTo(thisPlayer) > 8 || !otherPlayer.level().dimension().equals(level.dimension())){
			unlink(itemStack);
		}
		if (itemStack.has(DualStanceComponents.HEART_SEALED_CREST)) {
			if (!itemStack.get(DualStanceComponents.HEART_SEALED_CREST).isEmpty() && itemStack.has(DualStanceComponents.LINKED_CREST)) {
				var effects1 = itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().get(DualStanceComponents.CREST);
				var effects2 = itemStack.get(DualStanceComponents.LINKED_CREST);
				CrestHelper.applyCrestEffect(thisPlayer, effects1);
				CrestHelper.applyCrestEffect(thisPlayer, effects2);
				CrestHelper.renderLinkParticle(thisPlayer, otherPlayer);
			} else unlink(itemStack);
		}
	}

	// Linking code
	@Override
	public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand type) {
		if(!(target instanceof LivingEntity otherPlayer)) //TODO: Change back to player
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
		itemStack.set(DualStanceComponents.LINKED_PLAYER, new LinkedPlayerComponent(otherPlayer.getUUID()));
		otherItemStack.set(DualStanceComponents.LINKED_PLAYER, new LinkedPlayerComponent(player.getUUID()));
		itemStack.set(DualStanceComponents.LINKED_CREST, otherItemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().get(DualStanceComponents.CREST));
		otherItemStack.set(DualStanceComponents.LINKED_CREST, itemStack.get(DualStanceComponents.HEART_SEALED_CREST).crest().get(DualStanceComponents.CREST));
		otherItemStack.set(DataComponents.ITEM_NAME, Component.literal("rawr2"));
		itemStack.set(DataComponents.ITEM_NAME, Component.literal("rawr1"));
		player.makeSound(SoundEvents.PLAYER_LEVELUP);
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

	public static void unlink(ItemStack itemStack) {
		itemStack.remove(DualStanceComponents.LINKED_PLAYER);
		itemStack.remove(DualStanceComponents.LINKED_CREST);
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
		return !display.shows(DataComponents.BUNDLE_CONTENTS)
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
