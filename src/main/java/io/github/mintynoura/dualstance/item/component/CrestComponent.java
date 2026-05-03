package io.github.mintynoura.dualstance.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.crest_effects.AttributeCrestEffect;
import io.github.mintynoura.dualstance.item.component.crest_effects.CrestEffect;
import io.github.mintynoura.dualstance.item.component.crest_effects.MobEffectCrestEffect;
import io.github.mintynoura.dualstance.item.component.crest_effects.SidedCrestEffect;
import io.github.mintynoura.dualstance.registries.DualStanceComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.List;
import java.util.function.Consumer;

public record CrestComponent(Identifier id, List<CrestEffect> crestEffects) implements TooltipProvider {
	public static final Codec<CrestComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
		// id is for naming the crests and checked when applying combos
		Identifier.CODEC.optionalFieldOf("id", Identifier.fromNamespaceAndPath(DualStance.ID, "default")).forGetter(CrestComponent::id),
		CrestEffect.CODEC.listOf().optionalFieldOf("crest_effects", List.of()).forGetter(CrestComponent::crestEffects)
	).apply(builder, CrestComponent::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, CrestComponent> STREAM_CODEC = StreamCodec.composite(
		Identifier.STREAM_CODEC,
		CrestComponent::id,
		CrestEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),
		CrestComponent::crestEffects,
		CrestComponent::new
	);

	public static CrestComponent copy(CrestComponent crestComponent) {
		return new CrestComponent(crestComponent.id, crestComponent.crestEffects);
	}

	// this call is for crest items and linked crests
	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
		addToTooltip(true, context, consumer, flag, components);
	}

	// This method is called in HeartSealedCrest to handle double "When Bonded" texts
	// TODO: maybe evaluate SidedCrestEffect items, and prevent showing effects if denied
	public void addToTooltip(boolean withLinkedText, Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
		if (!this.crestEffects.isEmpty()) {
			// no player, with text (linked) -> bounded text
			// no player, without text (inner) -> bounded text
			// player, with text (linked) -> thanks to text
			// player, without text (inner) -> no text
			var linkedPlayer = components.get(DualStanceComponents.LINKED_MOB);
			if (linkedPlayer == null)
				consumer.accept(Component.translatableWithFallback("tooltip.dual_stance.crest_bond", "When bonded:").withStyle(ChatFormatting.GRAY));
			else if (withLinkedText)
				consumer.accept(Component.translatableWithFallback("tooltip.dual_stance.thanks_to", "Thanks to",linkedPlayer.name()).withStyle(ChatFormatting.GRAY));
			for (CrestEffect crestEffect : this.crestEffects) {
				if (crestEffect instanceof AttributeCrestEffect(List<AttributeCrestEffect.Entry> modifiers)) {
					for (AttributeCrestEffect.Entry entry : modifiers) {
						AttributeCrestEffect.display(consumer, entry.attribute(), entry.modifier());
					}
				}
				if (crestEffect instanceof MobEffectCrestEffect(
					List<MobEffectInstance> effects, int interval
					, _, _
				)) {
					MobEffectCrestEffect.display(effects, consumer, interval);
				}
				if (crestEffect instanceof SidedCrestEffect(CrestEffect crestEffect1, SidedCrestEffect.Side side, _, _)) {
					if (!withLinkedText) {
						SidedCrestEffect.displayUnlinked(crestEffect1, side, consumer);
					} else SidedCrestEffect.displayLinked(crestEffect1, side, consumer);
				}
			}
		}
	}
}
