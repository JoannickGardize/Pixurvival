package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.BeanFactory;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.ClassNameCellRenderer;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.util.CachedSupplier;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class InstanceChangingElementEditor<E> extends ElementEditor<E> {

    @Getter
    @AllArgsConstructor
    public class ClassEntry {
        private Class<? extends E> type;
        private Supplier<JPanel> specificPanel;
    }

    private static final long serialVersionUID = 1L;

    private JComboBox<Class<? extends E>> typeChooser;
    private @Getter JPanel specificPartPanel;
    private Map<Class<? extends E>, CachedSupplier<JPanel>> classEntries = new HashMap<>();
    private Map<Class<?>, E> stash = new HashMap<>();
    private int previousElementId;

    protected InstanceChangingElementEditor(Class<? super E> defaultType, String translationPreffix) {
        this(defaultType, translationPreffix, null);
    }

    @SuppressWarnings("unchecked")
    protected InstanceChangingElementEditor(Class<? super E> defaultType, String translationPreffix, Object params) {
        super(defaultType);
        for (ClassEntry classEntry : getClassEntries(params)) {
            classEntries.put(classEntry.getType(), new CachedSupplier<>(classEntry.getSpecificPanel()));
        }
        typeChooser = new JComboBox<>(classEntries.keySet().stream().toArray(Class[]::new));
        typeChooser.setRenderer(new ClassNameCellRenderer(translationPreffix));
        specificPartPanel = new JPanel(new BorderLayout());
        typeChooser.addItemListener(e -> {
            if (typeChooser.isPopupVisible() && e.getStateChange() == ItemEvent.SELECTED) {
                Class<? extends E> type = (Class<? extends E>) e.getItem();
                if (getValue() != null) {
                    stash.put(getValue().getClass(), getValue());
                }
                E instance = stash.get(type);
                if (instance == null) {
                    instance = BeanFactory.newInstance(type);
                }
                changeInstance(instance);
            }
        });
    }

    public Component getTypeChooser() {
        return LayoutUtils.single(typeChooser);
    }

    private void changeInstance(E newInstance) {
        if (newInstance == null) {
            return;
        }
        E oldInstance = getValue();
        if (oldInstance != null) {
            initialize(oldInstance, newInstance);
        }
        setValue(newInstance);
        notifyValueChanged();
    }

    @Override
    protected void valueChanging() {
        Class<?> type = getValue().getClass();
        // Preload the panel to bind values
        classEntries.get(type).getNew();
    }

    @Override
    protected void valueChanged(ValueComponent<?> source) {
        if (source == this && getValue() != null) {
            Class<?> type = getValue().getClass();
            specificPartPanel.removeAll();
            specificPartPanel.add(classEntries.get(type).get(), BorderLayout.CENTER);
            specificPartPanel.revalidate();
            specificPartPanel.repaint();
            typeChooser.setSelectedItem(type);
            if (getValue() instanceof NamedIdentifiedElement && ((NamedIdentifiedElement) getValue()).getId() != previousElementId) {
                stash.clear();
                previousElementId = ((NamedIdentifiedElement) getValue()).getId();
            }
        }
    }

    @Override
    public boolean isValueValid(E value) {
        if (getValue() == null || value != null && getValue().getClass() != value.getClass()) {
            // Preload the panel to bind values
            if (value != null) {
                classEntries.get(value.getClass()).get();
            }
            boolean valid = super.isValueValid(value);
            if (getValue() != null) {
                classEntries.get(getValue().getClass()).get();
            }
            return valid;
        } else {
            return super.isValueValid(value);
        }
    }

    protected abstract List<ClassEntry> getClassEntries(Object params);

    protected abstract void initialize(E oldInstance, E newInstance);
}
