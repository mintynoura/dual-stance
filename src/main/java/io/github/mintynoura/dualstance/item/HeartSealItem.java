package io.github.mintynoura.dualstance.item;

import io.github.mintynoura.dualstance.item.component.HeartSealContents;
import io.github.mintynoura.dualstance.item.component.HeartSealTooltip;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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

import java.util.Optional;

public class HeartSealItem extends Item {
	public HeartSealItem(Properties properties) {
		super(properties);
	}
	//TODO: Implement adding crest
	@Override
	public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand type) {
		if(player.level().isClientSide() || !(target instanceof Player otherPlayer))
			return InteractionResult.FAIL;
		ItemStack otherItemStack = otherPlayer.getItemInHand(InteractionHand.MAIN_HAND);
		if(!(otherItemStack.getItem() instanceof HeartSealItem))
			return InteractionResult.FAIL;

		otherItemStack.set(DualStanceComponents.LINKED_PLAYER, player.getUUID());
		itemStack.set(DualStanceComponents.LINKED_PLAYER, otherPlayer.getUUID());
		player.makeSound(SoundEvents.ENDER_DRAGON_DEATH);
		// TODO: Add crest effects to each other's
		//otherItemStack.set(DualStanceComponents.LINKED_CREST, )

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
		return super.overrideOtherStackedOnMe(self, other, slot, clickAction, player, carriedItem);
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
