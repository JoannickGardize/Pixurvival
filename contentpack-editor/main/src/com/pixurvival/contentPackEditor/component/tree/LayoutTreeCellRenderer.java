package com.pixurvival.contentPackEditor.component.tree;

import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.IconService;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class LayoutTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof LayoutElement) {
            LayoutElement element = (LayoutElement) value;
            this.setText("(" + element.getType() + ") " + element.getElement().getName());
            Icon icon = IconService.getInstance().get(element.getElement());
            if (icon == null) {
                icon = IconService.getInstance().get(ElementType.of(element.getElement()));
            }
            setIcon(icon);
        }
        this.setForeground(((LayoutNode) value).isValid() ? getForeground() : Color.RED);
        return this;
    }

}
