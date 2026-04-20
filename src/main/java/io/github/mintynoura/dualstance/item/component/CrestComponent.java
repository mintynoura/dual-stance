package io.github.mintynoura.dualstance.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mintynoura.dualstance.DualStance;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
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

	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
		if (!this.crestEffects.isEmpty()) {
			consumer.accept(Component.translatableWithFallback("tooltip.dual_stance.crest_bond", "When bonded:").withStyle(ChatFormatting.GRAY));
			for (CrestEffect crestEffect : this.crestEffects) {
				if (crestEffect instanceof AttributeCrestEffect(List<AttributeCrestEffect.Entry> modifiers)) {
					for (AttributeCrestEffect.Entry entry : modifiers) {
						AttributeCrestEffect.display(consumer, entry.attribute(), entry.modifier());
					}
				}
				if (crestEffect instanceof MobEffectCrestEffect mobEffectCrestEffect) {
					MobEffectCrestEffect.display(mobEffectCrestEffect.effects(), consumer);
				}
			}
		}
	}
}
