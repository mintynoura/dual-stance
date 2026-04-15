package io.github.mintynoura.dualstance.item.component;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.util.DualStanceTags;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipProvider;
import org.apache.commons.lang3.math.Fraction;
import org.jspecify.annotations.Nullable;

public final class HeartSealContents implements TooltipComponent, TooltipProvider {
	public static final HeartSealContents EMPTY = new HeartSealContents(List.of());
	public static final Codec<HeartSealContents> CODEC = ItemStackTemplate.CODEC.listOf().xmap(HeartSealContents::new, contents -> contents.items);
	public static final StreamCodec<RegistryFriendlyByteBuf, HeartSealContents> STREAM_CODEC = ItemStackTemplate.STREAM_CODEC
		.apply(ByteBufCodecs.list())
		.map(HeartSealContents::new, contents -> contents.items);
	private final List<ItemStackTemplate> items;
	private final int selectedItem;
	private final Supplier<DataResult<Fraction>> weight;

	private HeartSealContents(final List<ItemStackTemplate> items, final int selectedItem) {
		this.items = items;
		this.selectedItem = selectedItem;
		this.weight = Suppliers.memoize(() -> computeContentWeight(this.items));
	}

	public HeartSealContents(final List<ItemStackTemplate> items) {
		this(items, -1);
	}

	private static DataResult<Fraction> computeContentWeight(final List<? extends ItemInstance> items) {
			Fraction weight = Fraction.ZERO;
			for (ItemInstance stack : items) {
				DataResult<Fraction> itemWeight = getWeight(stack);
				if (itemWeight.isError()) {
					return itemWeight;
				}

				weight = weight.add(itemWeight.getOrThrow().multiplyBy(Fraction.getFraction(stack.count(), 1)));
			}

			return DataResult.success(weight);

	}

	private static DataResult<Fraction> getWeight(final ItemInstance item) {
		return DataResult.success(Fraction.ONE);
	}

	public static boolean canItemBeInHeartSeal(final ItemStack itemToAdd) {
		return itemToAdd.is(DualStanceTags.Items.CRESTS);
	}

	public Stream<ItemStack> itemCopyStream() {
		return this.items.stream().map(ItemStackTemplate::create);
	}

	public List<ItemStackTemplate> items() {
		return this.items;
	}

	public int size() {
		return this.items.size();
	}

	public DataResult<Fraction> weight() {
		return this.weight.get();
	}

	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else {
			return obj instanceof HeartSealContents contents && this.items.equals(contents.items);
		}
	}

	public int hashCode() {
		return this.items.hashCode();
	}

	public String toString() {
		return "HeartSealContents" + this.items;
	}

	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
		if (!this.isEmpty()) {
			if (this.items.getFirst().get(DualStanceComponents.CREST) != null) {
				this.items.getFirst().get(DualStanceComponents.CREST).addToTooltip(context, consumer, flag, components);
			}
		}
	}

	public static class Mutable {
		private final List<ItemStack> items;
		private Fraction weight;
		private int selectedItem;

		public Mutable(final HeartSealContents contents) {
			DataResult<Fraction> currentWeight = contents.weight.get();
			if (currentWeight.isError()) {
				this.items = new ArrayList<>();
				this.weight = Fraction.ZERO;
				this.selectedItem = -1;
			} else {
				this.items = new ArrayList<>(contents.items.size());

				for (ItemStackTemplate item : contents.items) {
					this.items.add(item.create());
				}

				this.weight = currentWeight.getOrThrow();
				this.selectedItem = contents.selectedItem;
			}
		}

		public HeartSealContents.Mutable clearItems() {
			this.items.clear();
			this.weight = Fraction.ZERO;
			this.selectedItem = -1;
			return this;
		}

		private int findStackIndex(final ItemStack itemsToAdd) {
			if (itemsToAdd.isStackable()) {
				for (int i = 0; i < this.items.size(); i++) {
					if (ItemStack.isSameItemSameComponents(this.items.get(i), itemsToAdd)) {
						return i;
					}
				}

			}
			return -1;
		}

		private int getMaxAmountToAdd(final Fraction itemWeight) {
			Fraction remainingWeight = Fraction.ONE.subtract(this.weight);
			return Math.max(remainingWeight.divideBy(itemWeight).intValue(), 0);
		}

		public int tryInsert(final ItemStack itemsToAdd) {
			if (!HeartSealContents.canItemBeInHeartSeal(itemsToAdd)) {
				return 0;
			} else {
				DataResult<Fraction> maybeItemWeight = HeartSealContents.getWeight(itemsToAdd);
				if (maybeItemWeight.isError()) {
					return 0;
				} else {
					Fraction itemWeight = maybeItemWeight.getOrThrow();
					int amountToAdd = Math.min(itemsToAdd.getCount(), this.getMaxAmountToAdd(itemWeight));
					if (amountToAdd == 0) {
						return 0;
					} else {
						this.weight = this.weight.add(itemWeight.multiplyBy(Fraction.getFraction(amountToAdd, 1)));
						int stackIndex = this.findStackIndex(itemsToAdd);
						if (stackIndex != -1) {
							ItemStack removedStack = this.items.remove(stackIndex);
							ItemStack mergedStack = removedStack.copyWithCount(removedStack.getCount() + amountToAdd);
							itemsToAdd.shrink(amountToAdd);
							this.items.addFirst(mergedStack);
						} else {
							this.items.addFirst(itemsToAdd.split(amountToAdd));
						}

						return amountToAdd;
					}
				}
			}
		}

		public int tryTransfer(final Slot slot, final Player player) {
			ItemStack other = slot.getItem();
			DataResult<Fraction> itemWeight = HeartSealContents.getWeight(other);
			if (itemWeight.isError()) {
				return 0;
			} else {
				int maxAmount = this.getMaxAmountToAdd(itemWeight.getOrThrow());
				return HeartSealContents.canItemBeInHeartSeal(other) ? this.tryInsert(slot.safeTake(other.getCount(), maxAmount, player)) : 0;
			}
		}

		public void toggleSelectedItem(final int selectedItem) {
			this.selectedItem = this.selectedItem != selectedItem && !this.indexIsOutsideAllowedBounds(selectedItem) ? selectedItem : -1;
		}

		private boolean indexIsOutsideAllowedBounds(final int selectedItem) {
			return selectedItem < 0 || selectedItem >= this.items.size();
		}

		@Nullable
		public ItemStack removeOne() {
			if (this.items.isEmpty()) {
				return null;
			} else {
				int removeIndex = this.indexIsOutsideAllowedBounds(this.selectedItem) ? 0 : this.selectedItem;
				ItemStack stack = this.items.remove(removeIndex).copy();
				this.weight = this.weight.subtract(HeartSealContents.getWeight(stack).getOrThrow().multiplyBy(Fraction.getFraction(stack.getCount(), 1)));
				this.toggleSelectedItem(-1);
				return stack;
			}
		}

		public Fraction weight() {
			return this.weight;
		}

		public HeartSealContents toImmutable() {
			Builder<ItemStackTemplate> builder = ImmutableList.builder();

			for (ItemStack item : this.items) {
				builder.add(ItemStackTemplate.fromNonEmptyStack(item));
			}

			return new HeartSealContents(builder.build(), this.selectedItem);
		}
	}
}
