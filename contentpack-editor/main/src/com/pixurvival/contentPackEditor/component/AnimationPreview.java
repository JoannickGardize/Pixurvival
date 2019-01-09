package com.pixurvival.contentPackEditor.component;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Getter;
import lombok.Setter;

public class AnimationPreview extends JPanel {

	private static final long serialVersionUID = 1L;

	private @Getter @Setter SpriteSheet spriteSheet;
	private @Getter Animation animation;
	private Object syncLock = new Object();
	private int currentIndex;

	public AnimationPreview() {
		new Thread(this::runAnimation).start();
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		synchronized (syncLock) {
			if (animation == null || !ContentPackEditionService.getInstance().isValidForPreview(spriteSheet)
					|| currentIndex >= animation.getFrames().size()) {
				return;
			}
			ResourceEntry resource = ResourcesService.getInstance().getResource(spriteSheet.getImage());
			if (resource == null || !(resource.getPreview() instanceof Image)) {
				return;
			}
			Image image = (Image) resource.getPreview();
			Rectangle rec = LayoutUtils.getCenteredKeepRatioRectangle(getWidth(), getHeight(), spriteSheet.getWidth(),
					spriteSheet.getHeight());
			Frame frame = animation.getFrames().get(currentIndex);
			int imageX = frame.getX() * spriteSheet.getWidth();
			int imageY = frame.getY() * spriteSheet.getHeight();
			g.drawImage(image, rec.x, rec.y, rec.x + rec.width, rec.y + rec.height, imageX, imageY,
					imageX + spriteSheet.getWidth(), imageY + spriteSheet.getHeight(), null);
		}
	}

	public void setAnimation(Animation animation) {
		synchronized (syncLock) {
			this.animation = animation;
		}
	}

	private void runAnimation() {
		try {
			double frameDuration = 1;
			long previousTime = System.currentTimeMillis();
			while (true) {
				synchronized (syncLock) {
					if (animation != null) {
						frameDuration = animation.getFrameDuration();
						long currentTime = System.currentTimeMillis();
						long elapsed = currentTime - previousTime;
						if (!animation.getFrames().isEmpty() && elapsed >= frameDuration * 1000) {
							previousTime = currentTime;
							currentIndex = (currentIndex + 1) % animation.getFrames().size();
						}
					} else {
						frameDuration = 1;
					}
					SwingUtilities.invokeLater(this::repaint);
				}
				long sleep = (int) (frameDuration * 1000);
				if (sleep > 500) {
					Thread.sleep(500);
				} else if (sleep > 50) {
					Thread.sleep(sleep);
				} else {
					Thread.sleep(50);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
