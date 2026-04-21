package io.github.mintynoura.dualstance.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.UUID;

public record LinkedPlayerComponent(UUID id, String name) {
	public static final Codec<LinkedPlayerComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
		UUIDUtil.CODEC.fieldOf("id").forGetter(LinkedPlayerComponent::id),
		ExtraCodecs.PLAYER_NAME.fieldOf("name").forGetter(LinkedPlayerComponent::name)
		).apply(builder, LinkedPlayerComponent::new)
	);
	public static final StreamCodec<ByteBuf, LinkedPlayerComponent> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		LinkedPlayerComponent::id,
		ByteBufCodecs.PLAYER_NAME,
		LinkedPlayerComponent::name,
		LinkedPlayerComponent::new
	);
}
