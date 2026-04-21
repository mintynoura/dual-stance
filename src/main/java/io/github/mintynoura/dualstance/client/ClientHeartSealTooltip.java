package io.github.mintynoura.dualstance.client;

import io.github.mintynoura.dualstance.item.component.HeartSealedCrest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ClientHeartSealTooltip implements ClientTooltipComponent {
	private static final Identifier SLOT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_background");
	private static final Component HEART_SEAL_EMPTY_DESCRIPTION = Component.translatableWithFallback("item.dual_stance.heart_seal.empty_description", "Attach a Crest to boost your bond");
	private final HeartSealedCrest crest;

	public ClientHeartSealTooltip(final HeartSealedCrest crest) {
		this.crest = crest;
	}

	@Override
	public int getHeight(final Font font) {
		return this.crest.isEmpty() ? getEmptyBackgroundHeight(font) : this.backgroundHeight();
	}

	@Override
	public int getWidth(final Font font) {
		return this.crest.isEmpty()? 90 : Math.max(90, font.width(this.crest.crest().getStyledHoverName()) + 24);
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
		if (this.crest.isEmpty()) {
			extractEmptyDescriptionText(x, y, w, font, graphics);
		} else {
			this.extractWithItemsTooltip(font, x, y, graphics);
		}
	}

	private void extractWithItemsTooltip(
		final Font font, final int x, final int y, final GuiGraphicsExtractor graphics
	) {
		ItemStack shownItem = this.crest.crest();
		if (shownItem != null) {
			this.extractCrest(x, y, shownItem, font, graphics);
		}
	}

	private void extractCrest(
		final int drawX,
		final int drawY,
		final ItemStack shownItem,
		final Font font,
		final GuiGraphicsExtractor graphics
	) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, drawX, drawY, 24, 24);
		graphics.item(shownItem, drawX + 4, drawY + 4, 0);
		graphics.itemDecorations(font, shownItem, drawX + 4, drawY + 4);
		Component itemName = shownItem.getStyledHoverName();
		int textWidth = font.width(itemName);
		graphics.textWithWordWrap(font, itemName, drawX + 24, drawY + 9, textWidth, -1);
	}

	private static void extractEmptyDescriptionText(final int x, final int y, final int w, final Font font, final GuiGraphicsExtractor graphics) {
		graphics.textWithWordWrap(font, HEART_SEAL_EMPTY_DESCRIPTION, x, y, w, -5592406);
	}

	private static int getEmptyDescriptionTextHeight(final Font font) {
		return font.split(HEART_SEAL_EMPTY_DESCRIPTION, 94).size() * 9;
	}
}
