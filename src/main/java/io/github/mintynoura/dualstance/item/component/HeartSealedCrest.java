package io.github.mintynoura.dualstance.item.component;

import com.mojang.serialization.Codec;

import java.util.function.Consumer;
import java.util.stream.Stream;

import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipProvider;

public record HeartSealedCrest(ItemStack crest) implements TooltipComponent, TooltipProvider {
	public static final HeartSealedCrest EMPTY = new HeartSealedCrest(ItemStack.EMPTY);
	public static final Codec<HeartSealedCrest> CODEC = ItemStack.CODEC.xmap(HeartSealedCrest::new, crest1 -> crest1.crest);
	public static final StreamCodec<RegistryFriendlyByteBuf, HeartSealedCrest> STREAM_CODEC = ItemStack.STREAM_CODEC
		.map(HeartSealedCrest::new, crest1 -> crest1.crest);

	public static boolean canItemBeInHeartSeal(final ItemStack itemToAdd) {
		return itemToAdd.has(DualStanceComponents.CREST);
	}

	public Stream<ItemStack> itemCopyStream() {
		return Stream.of(this.crest);
	}


	public boolean isEmpty() {
		return this.crest.isEmpty();
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else {
			return obj instanceof HeartSealedCrest(ItemStack crest1) && this.crest.equals(crest1);
		}
	}

	public String toString() {
		return "HeartSealedCrest" + this.crest;
	}

	// TODO: add linked buffs and combos here, fix linked crest "when bonded" line
	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
		if (!this.isEmpty()) {
			if (this.crest.has(DualStanceComponents.CREST)) {
				this.crest.get(DualStanceComponents.CREST).addToTooltip(context, consumer, flag, components);
			}
		}
	}

	public boolean canInsert(ItemStack itemStack) {
		return this.crest.isEmpty() && canItemBeInHeartSeal(itemStack);
	}
}
