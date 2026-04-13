package io.github.mintynoura.dualstance.item;

import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
}
