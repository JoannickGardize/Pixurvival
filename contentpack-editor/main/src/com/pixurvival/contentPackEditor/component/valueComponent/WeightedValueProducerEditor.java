package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.WeightedValueProducer;
import com.pixurvival.core.contentPack.WeightedValueProducer.Entry;

import java.io.Serializable;
import java.util.function.Supplier;

public class WeightedValueProducerEditor<E extends Serializable> extends ElementEditor<WeightedValueProducer<E>> {

    private static final long serialVersionUID = 1L;

    private ListEditor<Entry<E>> entriesEditor;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public WeightedValueProducerEditor(Class<E> elementType) {
        this(() -> new ElementChooserButton<>((Class) elementType), () -> null);
    }

    public WeightedValueProducerEditor(Supplier<ValueComponent<E>> elementEditor, Supplier<E> defaultValueSupplier) {
        super(WeightedValueProducer.class);
        entriesEditor = new HorizontalListEditor<>(() -> {
            WeightedValueEntryEditor<E> editor = new WeightedValueEntryEditor<>(elementEditor.get());
            editor.setBorder(LayoutUtils.createBorder());
            return editor;

        }, () -> {
            Entry<E> e = new Entry<>();
            e.setElement(defaultValueSupplier.get());
            e.setProbability(1);
            return e;
        });

        bind(entriesEditor, "backingArray");

        LayoutUtils.fill(this, entriesEditor);
    }
}
