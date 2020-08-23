package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.CollectionElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.elementSet.ExclusiveElementSet;
import com.pixurvival.core.contentPack.elementSet.InclusiveElementSet;

public class ElementSetEditor<T extends IdentifiedElement> extends InstanceChangingElementEditor<ElementSet<T>> {

	public ElementSetEditor(Class<T> type) {
		super("elementSetType", type);
		setLayout(new BorderLayout(2, 2));
		add(LayoutUtils.single(LayoutUtils.labelled("generic.type", getTypeChooser())), BorderLayout.NORTH);
		add(getSpecificPartPanel(), BorderLayout.CENTER);
	}

	private static final long serialVersionUID = 1L;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		Class<T> type = (Class<T>) params;
		List<ClassEntry> entries = new ArrayList<>();
		entries.add(new ClassEntry((Class) AllElementSet.class, JPanel::new));
		entries.add(new ClassEntry((Class) InclusiveElementSet.class, () -> {
			ListEditor<T> listEditor = new HorizontalListEditor<>(() -> new ElementChooserButton<>(type), () -> null);
			bind(listEditor, CollectionElementSet<T>::getElements, CollectionElementSet<T>::setElements, InclusiveElementSet.class);
			return listEditor;
		}));
		entries.add(new ClassEntry((Class) ExclusiveElementSet.class, () -> {
			ListEditor<T> listEditor = new HorizontalListEditor<>(() -> new ElementChooserButton<>(type), () -> null);
			bind(listEditor, CollectionElementSet<T>::getElements, CollectionElementSet<T>::setElements, ExclusiveElementSet.class);
			return listEditor;
		}));
		return entries;
	}

	@Override
	protected void initialize(ElementSet<T> oldInstance, ElementSet<T> newInstance) {
	}

}