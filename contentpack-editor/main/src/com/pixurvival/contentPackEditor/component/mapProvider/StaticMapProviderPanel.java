package com.pixurvival.contentPackEditor.component.mapProvider;

import java.awt.BorderLayout;
import java.util.function.Supplier;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ResourceFileChooser;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.map.StaticMapProvider;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.structure.Structure;

public class StaticMapProviderPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public StaticMapProviderPanel(MapProviderEditor mapProviderEditor) {

		ElementChooserButton<Tile> defaultTileChooser = new ElementChooserButton<>(Tile.class);

		mapProviderEditor.bind(defaultTileChooser, StaticMapProvider::getDefaultTile, StaticMapProvider::setDefaultTile, StaticMapProvider.class);

		setLayout(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
		TranslationService t = TranslationService.getInstance();

		JPanel tilePanel = new JPanel(new BorderLayout());
		tilePanel.add(LayoutUtils.single(LayoutUtils.labelled("mapGeneratorEditor.defaultTile", defaultTileChooser)), BorderLayout.NORTH);
		tilePanel.add(createMappingTab(mapProviderEditor, Tile.class,
				() -> mapProviderEditor.getValue() instanceof StaticMapProvider ? ((StaticMapProvider) mapProviderEditor.getValue()).getTilesImageResourceName() : null), BorderLayout.CENTER);
		tabbedPane.addTab(t.getString("elementType.tile"), tilePanel);
		tabbedPane.addTab(t.getString("elementType.structure"), createMappingTab(mapProviderEditor, Structure.class,
				() -> mapProviderEditor.getValue() instanceof StaticMapProvider ? ((StaticMapProvider) mapProviderEditor.getValue()).getStructuresImageResourceName() : null));
	}

	@SuppressWarnings("unchecked")
	public <T extends IdentifiedElement> JPanel createMappingTab(MapProviderEditor mapProviderEditor, Class<T> type, Supplier<String> imageResourceNameSupplier) {

		ResourceFileChooser resourceFileChooser = new ResourceFileChooser();

		JPanel panel = new JPanel(new BorderLayout(LayoutUtils.DEFAULT_GAP, LayoutUtils.DEFAULT_GAP));
		ImageMappingEditor<T> imageMappingEditor = new ImageMappingEditor<>(type);

		if (type == Tile.class) {
			mapProviderEditor.bind((ImageMappingEditor<Tile>) imageMappingEditor, StaticMapProvider::getTileMap, StaticMapProvider::setTileMap, StaticMapProvider.class);
			mapProviderEditor.bind(resourceFileChooser, StaticMapProvider::getTilesImageResourceName, (m, s) -> {
			}, StaticMapProvider.class);
		} else {
			mapProviderEditor.bind((ImageMappingEditor<Structure>) imageMappingEditor, StaticMapProvider::getStructureMap, StaticMapProvider::setStructureMap, StaticMapProvider.class);
			mapProviderEditor.bind(resourceFileChooser, StaticMapProvider::getStructuresImageResourceName, (m, s) -> {
			}, StaticMapProvider.class);
		}
		resourceFileChooser.setBorder(LayoutUtils.createBorder());
		imageMappingEditor.setBorder(LayoutUtils.createBorder());

		panel.add(resourceFileChooser, BorderLayout.NORTH);
		panel.add(imageMappingEditor, BorderLayout.CENTER);
		return panel;
	}

}
