package io.github.mintynoura.dualstance.item.component;

import io.github.mintynoura.dualstance.item.component.crest_effects.HeartSealedCrest;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record HeartSealTooltip(HeartSealedCrest crest) implements TooltipComponent {
}
