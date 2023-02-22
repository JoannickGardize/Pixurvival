package com.pixurvival.contentPackEditor.component.elementEditor;

import com.pixurvival.contentPackEditor.component.valueComponent.ValueChangeListener;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.util.BeanUtils;
import com.pixurvival.core.contentPack.validation.annotation.Nullable;
import com.pixurvival.core.util.ReflectionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An editor of Object, that contains {@link ValueComponent}s to edit its
 * attributes.
 *
 * @param <E> the type of object this editor is able to handle
 * @author SharkHendrix
 */
public class ElementEditor<E> extends JPanel implements ValueComponent<E> {

    private static final long serialVersionUID = 1L;

    private @Getter E value;
    private Map<String, ElementAttribute<E, Object>> attributes = new LinkedHashMap<>();
    private List<ValueChangeListener<E>> listeners = new ArrayList<>();
    private boolean nullable = false;
    private Class<? super E> type;
    private @Setter Predicate<E> additionalConstraint = f -> true;
    private @Getter
    @Setter JLabel associatedLabel;

    public ElementEditor(Class<? super E> type) {
        this.type = type;
    }

    @Override
    public final void setValue(E value) {
        setValue(value, false);
    }

    public void setValue(E value, boolean sneaky) {
        this.value = value;
        if (value != null) {
            valueChanging();
            for (ElementAttribute<E, Object> attribute : attributes.values()) {
                if (attribute.getCondition().test(value)) {
                    attribute.getComponent().setValue(attribute.getGetter().apply(value));
                }
            }
        }
        if (!sneaky) {
            valueChanged(this);
        }
        updateLabel();
    }

    @Override
    public boolean isValueValid(E value) {
        if (value == null) {
            return nullable;
        }
        for (ElementAttribute<E, Object> attribute : attributes.values()) {
            if (attribute.getCondition().test(value) && !attribute.getComponent().isValueValid(attribute.getGetter().apply(value))) {
                return false;
            }
        }
        return additionalConstraint.test(value);
    }

    @Override
    public void addValueChangeListener(ValueChangeListener<E> listener) {
        listeners.add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> ElementAttribute<E, T> bind(ValueComponent<T> component, String attributeName) {
        return bind(component, (Class<E>) type, attributeName);
    }

    @SuppressWarnings("unchecked")
    public <T> ElementAttribute<E, T> bind(ValueComponent<T> component, String attributeName, boolean bindField) {
        return bind(component, (Class<E>) type, attributeName, bindField);
    }

    public <F extends E, T> ElementAttribute<F, T> bind(ValueComponent<T> component, String attributeName, Class<F> type) {
        return bind(component, attributeName, type, true);
    }

    public <F extends E, T> ElementAttribute<F, T> bind(ValueComponent<T> component, String attributeName, Class<F> type, boolean bindField) {
        ElementAttribute<F, T> elementAttribute = bind(component, type, attributeName, bindField);
        elementAttribute.condition(type);
        return elementAttribute;
    }

    private <F extends E, T> ElementAttribute<F, T> bind(ValueComponent<T> component, Class<F> type, String attributeName) {
        return bind(component, type, attributeName, true);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private <F extends E, T> ElementAttribute<F, T> bind(ValueComponent<T> component, Class<F> type, String attributeName, boolean bindField) {
        ElementAttribute<F, T> elementAttribute = new ElementAttribute<>(component);
        if (bindField) {
            elementAttribute.getter((Function<F, T>) BeanUtils.getGetter(type, attributeName));
            elementAttribute.setter((BiConsumer<F, T>) BeanUtils.getSetter(type, attributeName));
            for (Annotation annotation : ReflectionUtils.getField(type, attributeName).getAnnotations()) {
                component.configure(annotation);
            }
        }
        elementAttribute.condition(e -> true);
        component.addValueChangeListener(v -> {
            if (value != null && elementAttribute.getCondition().test((F) value)) {
                elementAttribute.getSetter().accept((F) value, v);
                notifyValueChanged();
            }
            valueChanged(component);
            updateLabel();
        });
        attributes.put(attributeName, (ElementAttribute<E, Object>) elementAttribute);
        return elementAttribute;
    }

    public void notifyValueChanged() {
        listeners.forEach(l -> l.valueChanged(value));
    }

    protected void valueChanging() {
        // For override
    }

    /**
     * Called after the value of this editor has been changed and all sub fields has
     * been updated.
     *
     * @param value
     */
    protected void valueChanged(ValueComponent<?> source) {
        // optional overwrite
    }

    @Override
    public void configure(Annotation annotation) {
        if (annotation instanceof Nullable) {
            nullable = true;
        }
    }

    private void updateLabel() {
        if (associatedLabel != null) {
            if (isValueValid()) {
                associatedLabel.setForeground((Color.BLACK));
            } else {
                associatedLabel.setForeground((Color.RED));
            }
        }
    }
}
