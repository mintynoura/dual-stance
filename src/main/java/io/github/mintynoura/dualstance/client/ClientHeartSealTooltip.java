package io.github.mintynoura.dualstance.client;

import com.mojang.serialization.DataResult;
import java.util.List;

import io.github.mintynoura.dualstance.item.component.HeartSealContents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.apache.commons.lang3.math.Fraction;

@Environment(EnvType.CLIENT)
public class ClientHeartSealTooltip implements ClientTooltipComponent {
	private static final Identifier SLOT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_background");
	private static final Component HEART_SEAL_EMPTY_DESCRIPTION = Component.translatableWithFallback("item.dual_stance.heart_seal.empty_description", "Attach a Crest to boost your bond");
	private final HeartSealContents contents;

	public ClientHeartSealTooltip(final HeartSealContents contents) {
		this.contents = contents;
	}

	@Override
	public int getHeight(final Font font) {
		return this.contents.isEmpty() ? getEmptyBackgroundHeight(font) : this.backgroundHeight();
	}

	@Override
	public int getWidth(final Font font) {
		return 90;
	}

	@Override
	public boolean showTooltipWithItemInHand() {
		return true;
	}

	private static int getEmptyBackgroundHeight(final Font font) {
		return getEmptyDescriptionTextHeight(font);
	}

	private int backgroundHeight() {
		return 24;
	}

	@Override
	public void extractImage(final Font font, final int x, final int y, final int w, final int h, final GuiGraphicsExtractor graphics) {
		DataResult<Fraction> weight = this.contents.weight();
		if (!weight.isError()) {
			if (this.contents.isEmpty()) {
				extractEmptyDescriptionText(x, y, font, graphics);
			} else {
				this.extractWithItemsTooltip(font, x, y, w, graphics);
			}
		}
	}

	private void extractWithItemsTooltip(
		final Font font, final int x, final int y, final int w, final GuiGraphicsExtractor graphics
	) {
		ItemStackTemplate shownItem = this.contents.items().getFirst();
		if (shownItem != null) {
			this.extractSlot(x, y, shownItem, font, graphics);
		}

		this.extractCrestTooltip(font, graphics, x, y, w);
	}
	private void extractSlot(
		final int drawX,
		final int drawY,
		final ItemStackTemplate shownItem,
		final Font font,
		final GuiGraphicsExtractor graphics
	) {
		ItemStack item = shownItem.create();
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, drawX, drawY, 24, 24);
		graphics.item(item, drawX + 4, drawY + 4, 0);
		graphics.itemDecorations(font, item, drawX + 4, drawY + 4);
	}

	private void extractCrestTooltip(final Font font, final GuiGraphicsExtractor graphics, final int x, final int y, final int w) {
		ItemStackTemplate crestItem = this.contents.items().getFirst();
		if (crestItem != null) {
			ItemStack itemStack = crestItem.create();
			Component selectedItemName = itemStack.getStyledHoverName();
			int textWidth = font.width(selectedItemName.getVisualOrderText());
			int centerTooltip = x + w / 2 - 12;
			ClientTooltipComponent selectedItemNameTooltip = ClientTooltipComponent.create(selectedItemName.getVisualOrderText());
			graphics.tooltip(
				font,
				List.of(selectedItemNameTooltip),
				centerTooltip - textWidth / 2,
				y - 15,
				DefaultTooltipPositioner.INSTANCE,
				itemStack.get(DataComponents.TOOLTIP_STYLE)
			);
		}
	}

	private static void extractEmptyDescriptionText(final int x, final int y, final Font font, final GuiGraphicsExtractor graphics) {
		graphics.textWithWordWrap(font, HEART_SEAL_EMPTY_DESCRIPTION, x, y, 96, -5592406);
	}

	private static int getEmptyDescriptionTextHeight(final Font font) {
		return font.split(HEART_SEAL_EMPTY_DESCRIPTION, 96).size() * 9;
	}
}
