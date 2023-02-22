package com.pixurvival.contentPackEditor.component;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.sprite.Frame;

import java.awt.*;

public class ImageFramePreview extends ResourcePreview {

    private static final long serialVersionUID = 1L;

    public void setImage(String image) {
        ResourceEntry entry = ResourcesService.getInstance().getResource(image);
        if (entry == null) {
            setObject(null);
        } else {
            setObject(entry.getPreview());
        }
        repaint();

    }

    public void setFrame(Frame frame) {
        setRectangle(
                new Rectangle(frame.getX() * GameConstants.PIXEL_PER_UNIT, frame.getY() * GameConstants.PIXEL_PER_UNIT,
                        GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT));
        repaint();
    }
}
