package com.pixurvival.contentPackEditor.component.mapProvider;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.util.ColorUtils;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.map.ColorMapping;
import com.pixurvival.core.contentPack.map.ImageMapping;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageMappingEditor<T extends NamedIdentifiedElement> extends ElementEditor<ImageMapping<T>> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public ImageMappingEditor(Class<T> elementType) {
        super(ImageMapping.class);
        VerticalListEditor<ColorMapping<T>> listEditor = new VerticalListEditor<>(() -> new ColorMappingEditor<>(elementType), ColorMapping::new, VerticalListEditor.HORIZONTAL);
        listEditor.setAddOnButton(() -> new CPEButton("mapGeneratorEditor.addAllMissing", () -> {
            ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
            if (contentPack == null) {
                return;
            }
            List<T> elementList = (List<T>) ContentPackEditionService.getInstance().listOf(ElementType.of(elementType));
            List<ColorMapping<T>> mappingList = new ArrayList<>(listEditor.getValue());
            for (T element : elementList) {
                boolean found = false;
                for (ColorMapping<T> mapping : mappingList) {
                    if (mapping.getElement() == element) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Icon icon = IconService.getInstance().get(element);
                    Color color = Color.WHITE;
                    if (icon != null) {
                        color = ColorUtils.getAverageColor((BufferedImage) ((ImageIcon) icon).getImage());
                    }
                    mappingList.add(new ColorMapping<>(element, color.getRGB()));
                }
            }
            listEditor.setValue(mappingList);
            listEditor.notifyValueChanged();
        }));

        bind(listEditor, "colorMapping");

        setLayout(new BorderLayout());
        add(listEditor, BorderLayout.CENTER);
    }
}
