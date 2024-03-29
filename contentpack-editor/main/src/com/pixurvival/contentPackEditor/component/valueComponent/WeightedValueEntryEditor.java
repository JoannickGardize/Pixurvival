package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.core.contentPack.WeightedValueProducer.Entry;

import java.awt.*;
import java.io.Serializable;

public class WeightedValueEntryEditor<E extends Serializable> extends ElementEditor<Entry<E>> {

    private static final long serialVersionUID = 1L;

    public WeightedValueEntryEditor(ValueComponent<E> elementEditor) {
        super(Entry.class);

        FloatInput probability = new FloatInput();

        bind(elementEditor, "element");
        bind(probability, "probability");

        add((Component) elementEditor);
        add(probability);
    }
}
