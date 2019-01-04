package com.pixurvival.contentPackEditor.component.spriteSheet;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ImageService;
import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.ResourcePreview;
import com.pixurvival.contentPackEditor.component.util.InteractionListener;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.FrameOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class SpriteSheetPreview extends ResourcePreview {

	@Getter
	@AllArgsConstructor
	public static class ClickEvent {
		private int spriteX;
		private int spriteY;
		private int pixelX;
		private int pixelY;
	}

	private static final long serialVersionUID = 1L;

	private @Getter SpriteSheet spriteSheet;
	private Rectangle imageRectange;
	private boolean paintEquipmentOffset = true;
	private @Getter @Setter EquipmentOffset overrideEquipmentOffset;

	private List<InteractionListener> interactionListeners = new ArrayList<>();

	public SpriteSheetPreview() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Image image = (Image) getObject();
				if (image == null || imageRectange == null || !ContentPackEditionService.getInstance().isValidForPreview(spriteSheet)) {
					return;
				}
				double scale = imageRectange.width / image.getWidth(null);
				int x = (int) ((e.getX() - imageRectange.x) / (spriteSheet.getWidth() * scale));
				int y = (int) ((e.getY() - imageRectange.y) / (spriteSheet.getHeight() * scale));
				int pixelX = (int) ((e.getX() - imageRectange.x) / scale) - x * spriteSheet.getWidth();
				int pixelY = spriteSheet.getHeight() - ((int) ((e.getY() - imageRectange.y) / scale) - y * spriteSheet.getHeight()) - 1;
				int maxX = (int) (imageRectange.width / (spriteSheet.getWidth() * scale));
				int maxY = (int) (imageRectange.height / (spriteSheet.getHeight() * scale));
				if (x >= 0 && y >= 0 && x < maxX && y < maxY) {
					interactionListeners.forEach(l -> l.interactionPerformed(new ClickEvent(x, y, pixelX, pixelY)));
				}
			}
		});
	}

	public void addInteractionListener(InteractionListener listener) {
		interactionListeners.add(listener);
	}

	public void setSpriteSheet(SpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		if (spriteSheet != null) {
			ResourceEntry resourceEntry = ResourcesService.getInstance().getResource(spriteSheet.getImage());
			if (resourceEntry == null) {
				setObject(null);
			} else {
				setObject(resourceEntry.getPreview());
			}
		}
		super.paint(g);
	}

	@Override
	protected void paintOnTop(Graphics g, Rectangle rec) {
		if (spriteSheet == null) {
			return;
		}
		imageRectange = rec;
		Image image = (Image) getObject();
		double scaleX = 0;
		double scaleY = 0;
		if (spriteSheet.getWidth() > 0) {
			scaleX = (double) rec.width / image.getWidth(null);
			for (double x = rec.x; x <= rec.x + rec.width; x += spriteSheet.getWidth() * scaleX) {
				g.drawLine((int) x, rec.y, (int) x, rec.y + rec.height);
			}
		}
		if (spriteSheet.getHeight() > 0) {
			scaleY = (double) rec.height / image.getHeight(null);
			for (double y = rec.y; y <= rec.y + rec.height; y += spriteSheet.getHeight() * scaleY) {
				g.drawLine(rec.x, (int) y, rec.x + rec.width, (int) y);
			}
		}
		EquipmentOffset equipmentOffset = overrideEquipmentOffset != null ? overrideEquipmentOffset : spriteSheet.getEquipmentOffset();
		if (paintEquipmentOffset && spriteSheet.getWidth() > 0 && spriteSheet.getHeight() > 0 && equipmentOffset != null && equipmentOffset.getFrameOffsets() != null) {
			Image crossImage = ImageService.getInstance().get("cross");
			for (FrameOffset frameOffset : equipmentOffset.getFrameOffsets()) {
				int x = (int) (rec.x + frameOffset.getX() * spriteSheet.getWidth() * scaleX + frameOffset.getOffsetX() * scaleX);
				int y = (int) (rec.y + (frameOffset.getY() + 1) * spriteSheet.getHeight() * scaleY - (1 + frameOffset.getOffsetY()) * scaleY);
				g.drawImage(crossImage, x, y, (int) scaleX, (int) scaleY, null);

			}
		}
	}
}
