package com.pixurvival.contentPackEditor.component.valueComponent;

import javax.swing.*;
import java.lang.annotation.Annotation;

public interface ValueComponent<T> {

    T getValue();

    /**
     * Does not notify {@link ValueChangeListener}s
     *
     * @param value
     */
    void setValue(T value);

    default boolean isValueValid() {
        return isValueValid(getValue());
    }

    boolean isValueValid(T value);

    void setAssociatedLabel(JLabel label);

    JLabel getAssociatedLabel();

    void addValueChangeListener(ValueChangeListener<T> listener);

    /**
     * Configure this ValueComponent to match the given validation annotation from
     * the field this object represents. Does nothing by default.
     *
     * @param annotation the annotation to consider
     */
    default void configure(Annotation annotation) {
        // Nothing by default
    }
}
