package io.github.mintynoura.dualstance.item;

import io.github.mintynoura.dualstance.item.component.CrestComponent;
import io.github.mintynoura.dualstance.item.component.CrestEffect;
import io.github.mintynoura.dualstance.item.component.HeartSealContents;
import io.github.mintynoura.dualstance.item.component.HeartSealTooltip;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
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

	@Override
	public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
		super.inventoryTick(itemStack, level, owner, slot);
		if(!itemStack.has(DualStanceComponents.LINKED_PLAYER) || !(owner instanceof LivingEntity thisPlayer)) //TODO: Change to Player
			return;
		Player otherPlayer = level.getPlayerByUUID(itemStack.get(DualStanceComponents.LINKED_PLAYER));
		if (otherPlayer == null || otherPlayer.distanceTo(thisPlayer) > 8 || otherPlayer.level().dimension().equals(level.dimension())){
			itemStack.remove(DualStanceComponents.LINKED_PLAYER);
			itemStack.remove(DualStanceComponents.LINKED_CREST);
			return;
		}
		var effects1 = itemStack.get(DualStanceComponents.HEART_SEAL_CONTENTS).items().get(0).get(DualStanceComponents.CREST);
		var effects2 = itemStack.get(DualStanceComponents.LINKED_CREST);
		applyCrestEffect(thisPlayer, effects1);
		applyCrestEffect(thisPlayer, effects2);
	}

	public static void applyCrestEffect(Entity owner, CrestComponent effects){

	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand type) {
		if(!player.level().isClientSide() || !(target instanceof LivingEntity otherPlayer)) //TODO: Change back to player
			return InteractionResult.FAIL;
		ItemStack otherItemStack = otherPlayer.getItemInHand(InteractionHand.MAIN_HAND);
		if(!(otherItemStack.getItem() instanceof HeartSealItem))
			return InteractionResult.FAIL;
		if(!otherItemStack.has(DualStanceComponents.HEART_SEAL_CONTENTS) || !itemStack.has(DualStanceComponents.HEART_SEAL_CONTENTS))
			return InteractionResult.FAIL; //TODO: Figure out if this is the right result of this edge case.

		otherItemStack.set(DualStanceComponents.LINKED_PLAYER, player.getUUID());
		otherItemStack.set(DualStanceComponents.LINKED_CREST, itemStack.get(DualStanceComponents.CREST));
		otherItemStack.set(DataComponents.CUSTOM_NAME, Component.literal("rawr2"));
		itemStack.set(DualStanceComponents.LINKED_PLAYER, otherPlayer.getUUID());
		itemStack.set(DualStanceComponents.LINKED_CREST, otherItemStack.get(DualStanceComponents.CREST));
		itemStack.set(DataComponents.CUSTOM_NAME, Component.literal("rawr1"));
		player.makeSound(SoundEvents.ENDER_DRAGON_DEATH);


		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack self, Slot slot, ClickAction clickAction, Player player) {
		HeartSealContents initialContents = self.get(DualStanceComponents.HEART_SEAL_CONTENTS);
		if (initialContents == null) {
			return false;
		}else {
			ItemStack other = slot.getItem();
			HeartSealContents.Mutable contents = new HeartSealContents.Mutable(initialContents);
			if (clickAction == ClickAction.PRIMARY && !other.isEmpty()) {
				if (contents.tryTransfer(slot, player) > 0) {
					playInsertSound(player);
				} else {
					playInsertFailSound(player);
				}

				self.set(DualStanceComponents.HEART_SEAL_CONTENTS, contents.toImmutable());
				this.broadcastChangesOnContainerMenu(player);
				return true;
			} else if (clickAction == ClickAction.SECONDARY && other.isEmpty()) {
				ItemStack itemStack = contents.removeOne();
				if (itemStack != null) {
					ItemStack remainder = slot.safeInsert(itemStack);
					if (remainder.getCount() > 0) {
						contents.tryInsert(remainder);
					} else {
						playRemoveOneSound(player);
					}
				}

				self.set(DualStanceComponents.HEART_SEAL_CONTENTS, contents.toImmutable());
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
			toggleSelectedItem(self, -1);
			return false;
		} else {
			HeartSealContents initialContents = self.get(DualStanceComponents.HEART_SEAL_CONTENTS);
			if (initialContents == null) {
				return false;
			} else {
				HeartSealContents.Mutable contents = new HeartSealContents.Mutable(initialContents);
				if (clickAction == ClickAction.PRIMARY && !other.isEmpty()) {
					if (slot.allowModification(player) && contents.tryInsert(other) > 0) {
						playInsertSound(player);
					} else {
						playInsertFailSound(player);
					}

					self.set(DualStanceComponents.HEART_SEAL_CONTENTS, contents.toImmutable());
					this.broadcastChangesOnContainerMenu(player);
					return true;
				} else if (clickAction == ClickAction.SECONDARY && other.isEmpty()) {
					if (slot.allowModification(player)) {
						ItemStack removed = contents.removeOne();
						if (removed != null) {
							playRemoveOneSound(player);
							carriedItem.set(removed);
						}
					}

					self.set(DualStanceComponents.HEART_SEAL_CONTENTS, contents.toImmutable());
					this.broadcastChangesOnContainerMenu(player);
					return true;
				} else {
					toggleSelectedItem(self, -1);
					return false;
				}
			}
		}
	}

	public static void toggleSelectedItem(final ItemStack stack, final int selectedItem) {
		HeartSealContents initialContents = stack.get(DualStanceComponents.HEART_SEAL_CONTENTS);
		if (initialContents != null) {
			HeartSealContents.Mutable contents = new HeartSealContents.Mutable(initialContents);
			contents.toggleSelectedItem(selectedItem);
			stack.set(DualStanceComponents.HEART_SEAL_CONTENTS, contents.toImmutable());
		}
	}

	@Override
	public void onDestroyed(final ItemEntity entity) {
		HeartSealContents contents = entity.getItem().get(DualStanceComponents.HEART_SEAL_CONTENTS);
		if (contents != null) {
			entity.getItem().set(DualStanceComponents.HEART_SEAL_CONTENTS, HeartSealContents.EMPTY);
			ItemUtils.onContainerDestroyed(entity, contents.itemCopyStream());
		}
	}

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
			: Optional.ofNullable(self.get(DualStanceComponents.HEART_SEAL_CONTENTS)).map(HeartSealTooltip::new);
	}

	private void broadcastChangesOnContainerMenu(final Player player) {
		AbstractContainerMenu containerMenu = player.containerMenu;
		if (containerMenu != null) {
			containerMenu.slotsChanged(player.getInventory());
		}
	}
}
