package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.elementSet.ExclusiveElementSet;
import com.pixurvival.core.contentPack.elementSet.InclusiveElementSet;
import com.pixurvival.core.util.CaseUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ElementSetEditor<T extends NamedIdentifiedElement> extends InstanceChangingElementEditor<ElementSet<T>> {

    public ElementSetEditor(Class<T> type) {
        super(ElementSet.class, "elementSetType", type);
        setLayout(new BorderLayout(2, 2));
        add(LayoutUtils.single(LayoutUtils.labelled("generic.type", getTypeChooser())), BorderLayout.NORTH);
        add(getSpecificPartPanel(), BorderLayout.CENTER);
        setBorder(LayoutUtils.createGroupBorder("elementType." + CaseUtils.pascalToCamelCase(type.getSimpleName())));
    }

    private static final long serialVersionUID = 1L;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected List<ClassEntry> getClassEntries(Object params) {
        Class<T> type = (Class<T>) params;
        List<ClassEntry> entries = new ArrayList<>();
        entries.add(new ClassEntry((Class) AllElementSet.class, JPanel::new));
        entries.add(new ClassEntry((Class) InclusiveElementSet.class, () -> {
            ListEditor<T> listEditor = new HorizontalListEditor<>(() -> new ElementChooserButton<>(type), () -> null);
            bind(listEditor, "elements", (Class) InclusiveElementSet.class);
            return listEditor;
        }));
        entries.add(new ClassEntry((Class) ExclusiveElementSet.class, () -> {
            ListEditor<T> listEditor = new HorizontalListEditor<>(() -> new ElementChooserButton<>(type), () -> null);
            bind(listEditor, "elements", (Class) ExclusiveElementSet.class);
            return listEditor;
        }));
        return entries;
    }

    @Override
    protected void initialize(ElementSet<T> oldInstance, ElementSet<T> newInstance) {
    }

}
