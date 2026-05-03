package io.github.mintynoura.dualstance.item.component.crest_effects;

import com.mojang.serialization.Codec;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import io.github.mintynoura.dualstance.util.CrestCombinations;
import io.github.mintynoura.dualstance.util.DualStanceTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.locale.Language;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
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
		return itemToAdd.has(DualStanceComponents.CREST) || itemToAdd.is(DualStanceTags.Items.CRESTS);
	}

	public Stream<ItemStack> itemCopyStream() {
		return Stream.of(this.crest);
	}

	public boolean isEmpty() {
		return this.crest.isEmpty();
	}

	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
		Language currentLanguage = Language.getInstance();
		if (this.isEmpty())
			return;
		if (this.crest.has(DualStanceComponents.CREST)) {
			this.crest.get(DualStanceComponents.CREST).addToTooltip(false, context, consumer, flag, components);
			if (components.get(DualStanceComponents.LINKED_CREST) != null) {
				if (!this.crest.get(DualStanceComponents.CREST).id().equals(components.get(DualStanceComponents.LINKED_CREST).id())) {
					List<CrestEffect> comboEffects = CrestCombinations.evaluateCombo(Set.of(this.crest.get(DualStanceComponents.CREST).id(), components.get(DualStanceComponents.LINKED_CREST).id()));
					if (!comboEffects.isEmpty()) {
						consumer.accept(Component.translatableWithFallback("tooltip.dual_stance.combo", "Crest Bonus!").withStyle(ChatFormatting.GRAY));
						String translationString = CrestCombinations.createTranslationString(this.crest.get(DualStanceComponents.CREST).id(), components.get(DualStanceComponents.LINKED_CREST).id());
						if (currentLanguage.getOrDefault(translationString).length() <= 50) {
							consumer.accept(Component.translatable(translationString).withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC));
						} else {
							Minecraft.getInstance().font.getSplitter().splitLines(FormattedText.of(currentLanguage.getOrDefault(translationString)), currentLanguage.getOrDefault(translationString).length(), Style.EMPTY, (text, _) ->
								consumer.accept(Component.literal(text.getString()).withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC)));
						}
					}
				}
			}
		}
	}

	public boolean canInsert(ItemStack itemStack) {
		return this.crest.isEmpty() && canItemBeInHeartSeal(itemStack);
	}
}
