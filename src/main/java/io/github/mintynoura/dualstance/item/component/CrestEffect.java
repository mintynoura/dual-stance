package io.github.mintynoura.dualstance.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.mintynoura.dualstance.DualStance;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface CrestEffect {
	Codec<CrestEffect> CODEC = Type.CREST_EFFECT_TYPE_REGISTRY.byNameCodec().dispatch("type", CrestEffect::getType, Type::codec);
	StreamCodec<RegistryFriendlyByteBuf, CrestEffect> STREAM_CODEC = ByteBufCodecs.registry(Type.CREST_EFFECT_TYPE_KEY).dispatch(CrestEffect::getType, CrestEffect.Type::streamCodec);

	void trigger(Level level, LivingEntity entity);

	CrestEffect.Type<? extends CrestEffect> getType();

	record Type<T extends CrestEffect>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
		public static final ResourceKey<Registry<Type<?>>> CREST_EFFECT_TYPE_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(DualStance.ID, "crest_effect_types"));
		public static final Registry<Type<?>> CREST_EFFECT_TYPE_REGISTRY = FabricRegistryBuilder.create(CREST_EFFECT_TYPE_KEY).buildAndRegister();
	}
}
