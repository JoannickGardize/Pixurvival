package com.pixurvival.contentPackEditor.component.mapProvider;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.util.ColorUtils;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.map.ColorMapping;
import com.pixurvival.core.contentPack.map.ImageMapping;

public class ImageMappingEditor<T extends IdentifiedElement> extends ElementEditor<ImageMapping<T>> {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public ImageMappingEditor(Class<T> elementType) {
		VerticalListEditor<ColorMapping<T>> listEditor = new VerticalListEditor<>(() -> new ColorMappingEditor<>(elementType), ColorMapping::new);
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

		bind(listEditor, ImageMapping::getColorMapping, ImageMapping::setColorMapping);

		setLayout(new BorderLayout());
		add(listEditor, BorderLayout.CENTER);
	}
}
