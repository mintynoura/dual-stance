package io.github.mintynoura.dualstance.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record LinkedMobComponent(UUID id, String name) {
	public static final Codec<LinkedMobComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
		UUIDUtil.CODEC.fieldOf("id").forGetter(LinkedMobComponent::id),
		Codec.STRING.fieldOf("name").forGetter(LinkedMobComponent::name)
		).apply(builder, LinkedMobComponent::new)
	);
	public static final StreamCodec<ByteBuf, LinkedMobComponent> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		LinkedMobComponent::id,
		ByteBufCodecs.STRING_UTF8,
		LinkedMobComponent::name,
		LinkedMobComponent::new
	);
}
