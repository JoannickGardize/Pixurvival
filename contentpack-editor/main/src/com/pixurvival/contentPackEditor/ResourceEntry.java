package com.pixurvival.contentPackEditor;

import com.pixurvival.contentPackEditor.component.util.GraphicsUtils;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Getter
public class ResourceEntry extends NamedIdentifiedElement {

    private static final long serialVersionUID = 1L;

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
            icon = GraphicsUtils.createIcon((Image) preview);
        }

    }
}
