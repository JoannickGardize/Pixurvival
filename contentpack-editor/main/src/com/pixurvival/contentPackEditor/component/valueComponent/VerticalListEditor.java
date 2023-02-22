package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.ImageService;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class VerticalListEditor<E> extends ListEditor<E> {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final long serialVersionUID = 1L;

    private int buttonAlignment;

    private transient @Setter Supplier<Component> addOnButton = () -> null;

    public <F extends E> VerticalListEditor(Supplier<ValueComponent<F>> elementEditorSupplier, Supplier<F> valueSupplier) {
        this(elementEditorSupplier, valueSupplier, VERTICAL);
    }

    public <F extends E> VerticalListEditor(Supplier<ValueComponent<F>> elementEditorSupplier, Supplier<F> valueSupplier, int buttonAlignment) {
        this(elementEditorSupplier, valueSupplier, buttonAlignment, true);
    }

    public <F extends E> VerticalListEditor(Supplier<ValueComponent<F>> elementEditorSupplier, Supplier<F> valueSupplier, int buttonAlignment, boolean useScrollPane) {
        super(elementEditorSupplier, valueSupplier);
        this.buttonAlignment = buttonAlignment;
        listPanel.setBackground(new Color(150, 150, 150));
        setLayout(new BorderLayout());
        listPanel.setLayout(new GridBagLayout());
        listPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
        listPanel.setMinimumSize(new Dimension(200, 100));
        listPanel.setSize(200, 100);

        if (useScrollPane) {
            JPanel pusherPanel = new JPanel(new BorderLayout());
            pusherPanel.add(listPanel, BorderLayout.NORTH);
            pusherPanel.add(new JPanel(), BorderLayout.CENTER);
            add(new JScrollPane(pusherPanel), BorderLayout.CENTER);
        } else {
            add(listPanel, BorderLayout.CENTER);
        }
    }

    @Override
    protected void addEditor(ValueComponent<E> editor) {
        ((JComponent) editor).setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(3, 2, 3, 2), ((JComponent) editor).getBorder()));
        int index = listPanel.getComponentCount();
        if (index > 0) {
            JComponent lastComponent = (JComponent) listPanel.getComponent(index - 1);
            if (lastComponent instanceof JButton || !(LayoutUtils.componentAtIfExists(lastComponent, 0) instanceof ValueComponent)) {
                listPanel.remove(--index);
            }
        }

        final int finalIndex = index;
        CPEButton upButton = new CPEButton(ImageService.getInstance().get("up"), () -> {
            if (finalIndex > 0) {
                List<E> newValue = new ArrayList<>(getValue());
                E tmp = newValue.get(finalIndex - 1);
                newValue.set(finalIndex - 1, newValue.get(finalIndex));
                newValue.set(finalIndex, tmp);
                reindex(newValue);
                setValue(newValue);
                notifyValueChanged();
            }
        });
        CPEButton removeButton = new CPEButton(ImageService.getInstance().get("remove"), () -> {
            List<E> newValue = new ArrayList<>(getValue());
            newValue.remove(finalIndex);
            reindex(newValue);
            setValue(newValue);
            notifyValueChanged();
        });
        CPEButton downButton = new CPEButton(ImageService.getInstance().get("down"), () -> {
            if (finalIndex < getValue().size() - 1) {
                List<E> newValue = new ArrayList<>(getValue());
                E tmp = newValue.get(finalIndex + 1);
                newValue.set(finalIndex + 1, newValue.get(finalIndex));
                newValue.set(finalIndex, tmp);
                reindex(newValue);
                setValue(newValue);
                notifyValueChanged();
            }
        });
        LayoutUtils.setFixedSize(upButton, 30, 30);
        LayoutUtils.setFixedSize(removeButton, 30, 30);
        LayoutUtils.setFixedSize(downButton, 30, 30);

        JPanel buttonsPanel = LayoutUtils.single(buttonAlignment == VERTICAL ? LayoutUtils.createVerticalBox(upButton, removeButton, downButton)
                : LayoutUtils.createHorizontalBox(upButton, removeButton, downButton));

        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(3, 2, 3, 2);
        gbc.gridy = index;
        listPanel.add(LayoutUtils.createHorizontalBox(0, (Component) editor, buttonsPanel), gbc);
    }

    @Override
    protected void removeLast() {
        listPanel.remove(listPanel.getComponentCount() - 1);
    }

    @Override
    protected void endModifications() {
        addAddButton();
    }

    private void addAddButton() {
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(3, 2, 3, 2);
        gbc.gridy = listPanel.getComponentCount();
        JButton addButton = new CPEButton("generic.add", () -> add(valueSupplier.get()));
        Component addOnComp = addOnButton.get();
        if (addOnComp == null) {
            listPanel.add(addButton, gbc);
        } else {
            listPanel.add(LayoutUtils.createHorizontalBox(addButton, addOnComp), gbc);
        }
    }

    private void reindex(List<E> list) {
        if (!list.isEmpty() && list.get(0) instanceof NamedIdentifiedElement) {
            for (int i = 0; i < list.size(); i++) {
                ((NamedIdentifiedElement) list.get(i)).setId(i);
            }
        }
    }
}
