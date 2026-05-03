package io.github.mintynoura.dualstance.item.component.crest_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.registries.CrestEffectTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public record SidedCrestEffect(CrestEffect crestEffect, Side side, HolderSet<Item> items, boolean deny) implements CrestEffect {
	public static final MapCodec<SidedCrestEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		CrestEffect.CODEC.fieldOf("crest_effect").forGetter(SidedCrestEffect::crestEffect),
		Side.CODEC.fieldOf("side").forGetter(SidedCrestEffect::side),
		RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("items", HolderSet.empty()).forGetter(SidedCrestEffect::items),
		Codec.BOOL.optionalFieldOf("deny", false).forGetter(SidedCrestEffect::deny)
	).apply(builder, SidedCrestEffect::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SidedCrestEffect> STREAM_CODEC = StreamCodec.composite(
		CrestEffect.STREAM_CODEC,
		SidedCrestEffect::crestEffect,
		Side.STREAM_CODEC,
		SidedCrestEffect::side,
		ByteBufCodecs.holderSet(Registries.ITEM),
		SidedCrestEffect::items,
		ByteBufCodecs.BOOL,
		SidedCrestEffect::deny,
		SidedCrestEffect::new
	);

	public static void logNestedSidedCrestEffect(ItemStack itemStack) {
		DualStance.LOGGER.warn("Detected nested \"dual_stance:sided\" crest effect from item stack {}. Ignoring", itemStack);
	}

	public static void displayUnlinked(CrestEffect crestEffect, Side side, Consumer<Component> consumer) {
		switch (side) {
			case SELF ->  consumer.accept(Component.translatableWithFallback("tooltip.dual_stance.self_sided", "Only to self:").withStyle(ChatFormatting.DARK_GRAY));
			case PARTNER -> consumer.accept(Component.translatableWithFallback("tooltip.dual_stance.partner_sided", "Only to partner:").withStyle(ChatFormatting.DARK_GRAY));
		}
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
	}
	public static void displayLinked(CrestEffect crestEffect, Side side, Consumer<Component> consumer) {
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
	}

	@Override
	public void trigger(Level level, LivingEntity entity) {

	}

	@Override
	public Type<? extends CrestEffect> getType() {
		return CrestEffectTypes.SIDED_CREST_EFFECT_TYPE;
	}

	public enum Side implements StringRepresentable {
		SELF(0, "self"),
		PARTNER(1, "partner"),
		BOTH(2, "both");

		private final int id;
		private final String name;

		Side(int id, String name) {
			this.id = id;
			this.name = name;
		}
		private static final IntFunction<Side> BY_ID = ByIdMap.continuous(Side::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		public static final Codec<Side> CODEC = StringRepresentable.fromEnum(Side::values);
		public static final StreamCodec<ByteBuf, Side> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Side::getId);

		public int getId() {
			return this.id;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
