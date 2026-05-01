package io.github.mintynoura.dualstance.item;

import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.crest_effects.CrestEffect;
import io.github.mintynoura.dualstance.item.component.crest_effects.HeartSealedCrest;
import io.github.mintynoura.dualstance.item.component.crest_effects.PairUpRangeModifierCrestEffect;
import io.github.mintynoura.dualstance.registries.DualStanceGameRules;
import io.github.mintynoura.dualstance.item.component.*;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.registries.DualStanceItems;
import io.github.mintynoura.dualstance.registries.DualStanceSoundEvents;
import io.github.mintynoura.dualstance.util.CrestHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
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
		if (owner.getAttached(DualStance.PAIR_OFFER_TIMER) != null && owner.getAttached(DualStance.PAIR_OFFER_TIMER) > 0) {
			owner.setAttached(DualStance.PAIR_OFFER_TIMER, owner.getAttached(DualStance.PAIR_OFFER_TIMER)-1);
		}
		if (!itemStack.has(DualStanceComponents.LINKED_MOB) || !(owner instanceof Player thisPlayer)) return;
		Entity otherMob = level.getEntity(itemStack.get(DualStanceComponents.LINKED_MOB).id());
		if (otherMob == null || otherMob.distanceTo(thisPlayer) > getPairUpRange(itemStack, 8f) || !otherMob.level().dimension().equals(level.dimension()) || otherMob == owner) {
			unlinkSelf(itemStack, owner.asLivingEntity());
		}
		if (itemStack.has(DualStanceComponents.HEART_SEALED_CREST)) {
			if (itemStack.has(DualStanceComponents.LINKED_MOB)) {
				CrestHelper.tickCrestEffect(thisPlayer, itemStack);
				CrestHelper.renderLinkParticle(thisPlayer, otherMob);
			} else unlinkSelf(itemStack, owner.asLivingEntity());
		}
	}

	// Linking code
	@Override
	public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand type) {
		if (player.level() instanceof ServerLevel serverLevel) {
			if (!(target instanceof Player)) {
				if (serverLevel.getGameRules().get(DualStanceGameRules.ALLOW_PAIRING_WITH_MOBS) && itemStack.has(DualStanceComponents.HEART_SEALED_CREST)) {
					if (player.getInventory().countItem(DualStanceItems.HEART_SEAL) > 1)
						return InteractionResult.FAIL;
					if (!itemStack.get(DualStanceComponents.HEART_SEALED_CREST).isEmpty() && !itemStack.has(DualStanceComponents.LINKED_MOB)) {
						linkNonPlayerMob(itemStack, player, target);
						player.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
						return InteractionResult.SUCCESS_SERVER;
					} else return InteractionResult.FAIL;
				} else return InteractionResult.FAIL;
			} else if (target instanceof Player otherPlayer) {
				ItemStack otherItemStack = otherPlayer.getItemInHand(InteractionHand.MAIN_HAND);
				if(!(otherItemStack.getItem() instanceof HeartSealItem))
					return InteractionResult.FAIL;
				if (player.getInventory().countItem(DualStanceItems.HEART_SEAL) > 1)
					return InteractionResult.FAIL;
				if (!otherItemStack.has(DualStanceComponents.HEART_SEALED_CREST) || !itemStack.has(DualStanceComponents.HEART_SEALED_CREST) || otherItemStack.has(DualStanceComponents.LINKED_MOB) || itemStack.has(DualStanceComponents.LINKED_MOB)) {
					return InteractionResult.FAIL;
				}
				if (itemStack.get(DualStanceComponents.HEART_SEALED_CREST).isEmpty() || otherItemStack.get(DualStanceComponents.HEART_SEALED_CREST).isEmpty()) {
					return InteractionResult.FAIL;
				}
				if (otherPlayer.getAttachedOrElse(DualStance.PAIR_OFFER_TIMER, 0) > 0) {
					linkPlayer(itemStack, otherItemStack, player, otherPlayer);
					linkPlayer(otherItemStack, itemStack, otherPlayer, player);
					player.setAttached(DualStance.PAIR_OFFER_TIMER, 0);
					otherPlayer.setAttached(DualStance.PAIR_OFFER_TIMER, 0);
				} else {
					player.setAttached(DualStance.PAIR_OFFER_TIMER, 200);
					player.level().playSound(null, player.getOnPos(), DualStanceSoundEvents.PAIR_OFFER, SoundSource.PLAYERS);
					otherPlayer.sendOverlayMessage(Component.translatableWithFallback("overlay.dual_stance.pair_up_offer","%s wants to pair up!", player.getScoreboardName()));
				}
				player.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
				return InteractionResult.SUCCESS_SERVER;
			} else return InteractionResult.PASS;
		}
		return InteractionResult.PASS;
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
			} else if (clickAction == ClickAction.SECONDARY && other.isEmpty() && !sealedCrest.isEmpty() && !self.has(DualStanceComponents.LINKED_MOB)) {
				ItemStack slotStack = slot.safeInsert(sealedCrest.crest());
				if (slotStack != null) {
					playRemoveSound(player);
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
				} else if (clickAction == ClickAction.SECONDARY && other.isEmpty() && !self.has(DualStanceComponents.LINKED_MOB)) {
					if (slot.allowModification(player) && !sealedCrest.isEmpty()) {
						ItemStack removed = sealedCrest.crest();
						if (removed != null) {
							playRemoveSound(player);
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

	public static float getPairUpRange(ItemStack itemStack, float range) {
		for (CrestEffect crestEffect : CrestHelper.collectCrestEffects(itemStack)) {
			if (crestEffect instanceof PairUpRangeModifierCrestEffect(float modifier, boolean multiply)) {
				return multiply ? range * modifier : range + modifier;
			}
		}
		return range;
	}

	public static void linkPlayer(ItemStack itemStack, ItemStack otherItemStack, LivingEntity player, LivingEntity otherPlayer) {
		player.level().playSound(null, player.getOnPos(), DualStanceSoundEvents.PAIR_LINK, SoundSource.PLAYERS);
		CrestHelper.linkPlayer(itemStack, otherItemStack, player, otherPlayer);
	}

	public static void linkNonPlayerMob(ItemStack itemStack, LivingEntity player, LivingEntity mob) {
		player.level().playSound(null, player.getOnPos(), DualStanceSoundEvents.PAIR_LINK, SoundSource.PLAYERS);
		CrestHelper.linkNonPlayerMob(itemStack, player, mob);
	}

	public static void unlinkSelf(ItemStack itemStack, LivingEntity self) {
		self.level().playSound(null, self.getOnPos(), DualStanceSoundEvents.PAIR_UNLINK, SoundSource.PLAYERS);
		CrestHelper.unlink(itemStack, self);
	}

	private static void playRemoveSound(final Entity entity) {
		entity.playSound(DualStanceSoundEvents.HEART_SEAL_REMOVE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertSound(final Entity entity) {
		entity.playSound(DualStanceSoundEvents.HEART_SEAL_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertFailSound(final Entity entity) {
		entity.playSound(DualStanceSoundEvents.HEART_SEAL_INSERT_FAIL, 1.0F, 1.0F);
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
