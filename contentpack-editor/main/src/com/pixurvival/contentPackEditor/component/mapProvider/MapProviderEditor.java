package com.pixurvival.contentPackEditor.component.mapProvider;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingRootElementEditor;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.MapProvider;
import com.pixurvival.core.contentPack.map.ProcedurallyGeneratedMapProvider;
import com.pixurvival.core.contentPack.map.StaticMapProvider;

public class MapProviderEditor extends InstanceChangingRootElementEditor<MapProvider> {

	private static final long serialVersionUID = 1L;

	public MapProviderEditor() {
		super("mapProviderType");
		setLayout(new BorderLayout(0, LayoutUtils.DEFAULT_GAP));
		add(LayoutUtils.labelled("generic.type", getTypeChooser()), BorderLayout.NORTH);
		getSpecificPartPanel().setBorder(LayoutUtils.createBorder());
		add(getSpecificPartPanel(), BorderLayout.CENTER);
	}

	@Override
	public boolean isValueValid(MapProvider value) {
		if (value == null) {
			return false;
		}
		if (!(value instanceof ProcedurallyGeneratedMapProvider) || getValue() == value) {
			return super.isValueValid(value);
		}
		// Solves heightmap reference problem
		MapProvider previousValue = getValue();
		setValue(value, true);
		boolean result = super.isValueValid(value);
		setValue(previousValue, true);
		return result;
	}

	public Supplier<Collection<Heightmap>> getHeightmapCollectionSupplier() {
		return () -> {
			if (!(getValue() instanceof ProcedurallyGeneratedMapProvider)) {
				return Collections.emptyList();
			} else {
				return ((ProcedurallyGeneratedMapProvider) getValue()).getHeightmaps();
			}
		};
	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> entries = new ArrayList<>();
		entries.add(new ClassEntry(ProcedurallyGeneratedMapProvider.class, () -> new ProcedurallyGeneratedMapProviderPanel(this)));
		entries.add(new ClassEntry(StaticMapProvider.class, () -> new StaticMapProviderPanel(this)));
		return entries;
	}
}
