package io.github.mintynoura.dualstance.client;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.mintynoura.dualstance.DualStance;
import io.github.mintynoura.dualstance.item.component.HeartSealTooltip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public class DualStanceClient implements ClientModInitializer {

	private static final RenderPipeline LINKED_PLAYERS = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation("pipeline/leash")
			.withVertexShader("core/rendertype_leash")
			.withFragmentShader("core/rendertype_leash")
			.withSampler("Sampler2")
			.withCull(false)
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.TRIANGLE_STRIP)
			.withDepthStencilState(DepthStencilState.DEFAULT)
			.build()
	);

	@Override
	public void onInitializeClient() {
		ClientTooltipComponentCallback.EVENT.register(component ->{
			if (component instanceof HeartSealTooltip heartSealTooltip) {
				return new ClientHeartSealTooltip(heartSealTooltip.contents());
			} else return null;
		});

	}
}
