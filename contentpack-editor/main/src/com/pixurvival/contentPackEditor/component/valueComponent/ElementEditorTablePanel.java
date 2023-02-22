package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.util.Array2D;
import com.pixurvival.core.contentPack.sprite.Frame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ElementEditorTablePanel<T extends Frame> extends ElementEditor<List<T>> {

    private static final long serialVersionUID = 1L;

    private Array2D<ElementEditor<T>> cells = new Array2D<>(0, 0);
    private BiFunction<Integer, Integer, ElementEditor<T>> cellProducer;

    public ElementEditorTablePanel(BiFunction<Integer, Integer, ElementEditor<T>> cellProducer) {
        super(List.class);
        this.cellProducer = cellProducer;
    }

    public void setTableSize(int width, int height, boolean updateValue) {
        removeAll();
        cells.resize(width, height);
        if (getValue() == null) {
            setValue(new ArrayList<>());
        }
        if (width == 0 || height == 0) {
            setLayout(new BorderLayout());
            revalidate();
            repaint();
            return;
        }
        setLayout(new GridLayout(height, width));
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ElementEditor<T> elementEditor = cells.get(x, y);
                if (elementEditor == null) {
                    elementEditor = cellProducer.apply(x, y);
                    elementEditor.addValueChangeListener(e -> notifyValueChanged());
                    cells.set(x, y, elementEditor);
                }
                add(elementEditor);
            }
        }
        if (updateValue) {
            getValue().clear();
            cells.forEach(editor -> getValue().add(editor.getValue()));
            notifyValueChanged();
        } else {
            cells.forEach(new Consumer<ElementEditor<T>>() {
                private int i = 0;

                @Override
                public void accept(ElementEditor<T> editor) {
                    editor.setValue(getValue().get(i++));
                }
            });
        }

        revalidate();
        repaint();
    }

    public int getTableWidth() {
        return cells.getWidth();
    }

    public int getTableHeight() {
        return cells.getHeight();
    }

    public ElementEditor<T> getCell(int x, int y) {
        return cells.get(x, y);
    }

    @Override
    protected void valueChanged(ValueComponent<?> source) {
        int width = 0;
        int height = 0;
        for (T element : getValue()) {
            if (element.getX() + 1 > width) {
                width = element.getX() + 1;
            }
            if (element.getY() + 1 > height) {
                height = element.getY() + 1;
            }
        }
        setTableSize(width, height, false);
    }
}
