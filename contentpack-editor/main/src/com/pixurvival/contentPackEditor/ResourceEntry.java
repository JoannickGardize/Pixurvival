package com.pixurvival.contentPackEditor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.Getter;

@Getter
public class ResourceEntry extends NamedElement {

	private byte[] data;
	private Object preview;
	private ImageIcon icon;

	public ResourceEntry(String name, byte[] data) {
		super(name, 0);
		this.data = data;
		loadPreview();
		loadIcon();
	}

	private void loadPreview() {
		int dotIndex = getName().lastIndexOf('.');
		if (dotIndex != -1) {
			String extension = getName().substring(dotIndex + 1);
			if ("png".equalsIgnoreCase(extension)) {
				try {
					preview = ImageIO.read(new ByteArrayInputStream(data));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void loadIcon() {
		if (preview instanceof Image) {
			BufferedImage resizedImg = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = resizedImg.createGraphics();

			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage((Image) preview, 0, 0, 20, 20, null);
			g2.dispose();
			icon = new ImageIcon(resizedImg);
		}

	}
}
