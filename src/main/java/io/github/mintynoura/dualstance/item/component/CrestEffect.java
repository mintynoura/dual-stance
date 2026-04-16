package io.github.mintynoura.dualstance.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import io.github.mintynoura.dualstance.DualStance;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface CrestEffect {
	Codec<CrestEffect> CODEC = Type.REGISTRY.byNameCodec().dispatch("type", CrestEffect::getType, Type::codec);
	StreamCodec<RegistryFriendlyByteBuf, CrestEffect> STREAM_CODEC = ByteBufCodecs.registry(Type.REGISTRY.key()).dispatch(CrestEffect::getType, CrestEffect.Type::streamCodec);

	void trigger(Level level, LivingEntity entity, ItemStack itemStack);

	CrestEffect.Type<? extends CrestEffect> getType();

	record Type<T extends CrestEffect>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
		public static final Registry<Type<?>> REGISTRY = new MappedRegistry<>(
			ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(DualStance.ID, "crest_effect_types")), Lifecycle.stable());
	}

}
