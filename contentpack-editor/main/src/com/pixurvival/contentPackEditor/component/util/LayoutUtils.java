package com.pixurvival.contentPackEditor.component.util;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import lombok.experimental.UtilityClass;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.function.Supplier;

@UtilityClass
public class LayoutUtils {

    public static final int DEFAULT_GAP = 5;

    public static JLabel addHorizontalLabelledItem(Container parent, String labelKey, Component component, GridBagConstraints gbc) {
        String tooltipKey = labelKey + ".tooltip";
        if (!TranslationService.getInstance().containsKey(tooltipKey)) {
            tooltipKey = null;
        }
        return addHorizontalLabelledItem(parent, labelKey, tooltipKey, component, gbc);
    }

    public static JLabel addHorizontalLabelledItem(Container parent, String labelKey, String toolTipKey, Component component, GridBagConstraints gbc) {
        return addHorizontalLabelledItem(parent, labelKey, true, toolTipKey, component, gbc);
    }

    public static JLabel addHorizontalLabelledItem(Container parent, String labelKey, boolean useTranslation, String toolTipKey, Component component, GridBagConstraints gbc) {
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = 2;
        gbc.insets.bottom = 1;
        int previousAnchor = gbc.anchor;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        JLabel label = new JLabel(useTranslation ? TranslationService.getInstance().getString(labelKey) : labelKey);
        parent.add(label, gbc);
        gbc.anchor = previousAnchor;
        gbc.gridx++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        int previousLeft = gbc.insets.left;
        gbc.insets.left = DEFAULT_GAP;
        parent.add(component, gbc);
        gbc.insets.left = previousLeft;
        if (component instanceof ValueComponent) {
            ((ValueComponent<?>) component).setAssociatedLabel(label);
        } else if (component instanceof JPanel) {
            for (Component childComponent : ((JPanel) component).getComponents()) {
                if (childComponent instanceof ValueComponent) {
                    ((ValueComponent<?>) childComponent).setAssociatedLabel(label);

                }
            }
        }
        gbc.gridx--;
        gbc.gridy++;

        if (toolTipKey != null) {
            String tooltipText = TranslationService.getInstance().getString(toolTipKey);
            label.setToolTipText(tooltipText);
            if (component instanceof JComponent) {
                ((JComponent) component).setToolTipText(tooltipText);
            }
        }
        return label;
    }

    public static void nextColumn(GridBagConstraints gbc) {
        gbc.gridx += 2;
        gbc.gridy = 0;
        gbc.insets.left = DEFAULT_GAP;
    }

    public static void addHorizontalSeparator(Container parent, GridBagConstraints gbc) {
        gbc.gridwidth = 2;
        parent.add(new JSeparator(), gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
    }

    public static void addEmptyFiller(Container parent, GridBagConstraints gbc) {
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        parent.add(new JPanel(), gbc);
    }

    public static GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        return gbc;
    }

    public static void addSideBySide(Container parent, Component left, Component right) {
        parent.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        parent.add(left, gbc);
        gbc.gridx++;
        gbc.insets.left = DEFAULT_GAP;
        parent.add(right, gbc);

    }

    public static JPanel sideBySide(Component left, Component right) {
        JPanel result = new JPanel();
        addSideBySide(result, left, right);
        return result;
    }

    public static JPanel labelled(String labelKey, Component component) {
        String tooltipKey = labelKey + ".tooltip";
        JPanel panel = new JPanel(new BorderLayout(DEFAULT_GAP, 0));
        JLabel label = label(labelKey);
        panel.add(label, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        if (component instanceof ValueComponent) {
            ((ValueComponent<?>) component).setAssociatedLabel(label);
        } else if (((JComponent) component).getComponentCount() > 0 && ((JComponent) component).getComponent(0) instanceof ValueComponent) {
            ((ValueComponent<?>) ((JComponent) component).getComponent(0)).setAssociatedLabel(label);
        }
        if (TranslationService.getInstance().containsKey(tooltipKey)) {
            String toolTipText = TranslationService.getInstance().getString(tooltipKey);
            ((JComponent) component).setToolTipText(toolTipText);
            label.setToolTipText(toolTipText);
        }
        return panel;
    }

    public static JLabel label(String labelKey) {
        return new JLabel(TranslationService.getInstance().getString(labelKey));
    }

    public static Border createGroupBorder(String titlekey) {
        return BorderFactory.createTitledBorder(createBorder(), TranslationService.getInstance().getString(titlekey));
    }

    public static Border createBorder() {
        return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
                BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    }

    public static void addHorizontally(Container panel, Component... components) {
        addHorizontally(panel, -1, components);
    }

    public static void addHorizontally(Container panel, int fillIndex, Component... components) {
        addHorizontally(panel, fillIndex, DEFAULT_GAP, components);
    }

    public static void addHorizontally(Container panel, int fillIndex, int gap, Component... components) {
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        int defaultWeight = fillIndex == -1 ? 1 : 0;
        gbc.weightx = defaultWeight;
        gbc.weighty = 1;
        gbc.insets.right = gap;
        gbc.insets.left = gap;
        for (int i = 0; i < components.length; i++) {
            if (fillIndex == i) {
                gbc.weightx = 1;
            }
            panel.add(components[i], gbc);
            gbc.gridx++;
            gbc.insets.left = 0;
            gbc.weightx = defaultWeight;
        }
    }

    public static JPanel createHorizontalBox(int fillIndex, Component... components) {
        JPanel panel = new JPanel();
        addHorizontally(panel, fillIndex, DEFAULT_GAP, components);
        return panel;
    }

    public static void addHorizontallyLabelled(JPanel panel, Object... labelAndComponents) {
        panel.setLayout(new GridBagLayout());
        boolean nextIsLabel = true;
        GridBagConstraints gbc = createGridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.insets.right = DEFAULT_GAP;
        gbc.insets.left = DEFAULT_GAP;
        String nextLabelKey = null;
        for (int i = 0; i < labelAndComponents.length; i++) {
            Object nextEntry = labelAndComponents[i];
            if (nextEntry instanceof LayoutPropertyMarker) {
                ((LayoutPropertyMarker) nextEntry).apply(gbc);
            } else {
                if (nextIsLabel) {
                    nextLabelKey = (String) labelAndComponents[i];
                } else {
                    Component component = (Component) labelAndComponents[i];
                    panel.add(LayoutUtils.labelled(nextLabelKey, component), gbc);
                    gbc.insets.left = 0;
                    gbc.gridx++;
                }
                nextIsLabel = !nextIsLabel;
            }
        }
    }

    public static JPanel createHorizontalLabelledBox(Object... labelAndComponents) {
        JPanel result = new JPanel();
        addHorizontallyLabelled(result, labelAndComponents);
        return result;
    }

    public static JPanel createVerticalLabelledBox(Object... labelAndComponents) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        for (int i = 0; i < labelAndComponents.length; i += 2) {
            String labelKey = (String) labelAndComponents[i];
            Component component = (Component) labelAndComponents[i + 1];
            LayoutUtils.addHorizontalLabelledItem(panel, labelKey, component, gbc);
        }
        return panel;
    }

    public static JPanel createHorizontalBox(Component... components) {
        return createHorizontalBox(DEFAULT_GAP, components);
    }

    public static JPanel createVerticalBox(Component... components) {
        return createVerticalBox(DEFAULT_GAP, components);
    }

    public static JPanel createVerticalBox(int gap, Component... components) {
        return createVerticalBox(gap, -1, components);
    }

    public static JPanel createVerticalBox(int gap, int fillIndex, Component... components) {
        JPanel panel = new JPanel();
        addVertically(panel, gap, fillIndex, components);
        return panel;
    }

    public static void addVertically(JPanel panel, int gap, Component... components) {
        addVertically(panel, gap, -1, components);
    }

    public static void addVertically(JPanel panel, Component... components) {
        addVertically(panel, DEFAULT_GAP, components);
    }

    public static void addVertically(JPanel panel, int gap, int fillIndex, Component... components) {
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();
        gbc.insets.top = gap;
        gbc.fill = GridBagConstraints.BOTH;
        int defaultWeight = fillIndex == -1 ? 1 : 0;
        gbc.weighty = defaultWeight;
        gbc.weightx = 1;
        for (int i = 0; i < components.length; i++) {
            if (fillIndex == i) {
                gbc.weighty = 1;
            }
            if (i == components.length - 1) {
                gbc.insets.bottom = gap;
            }
            panel.add(components[i], gbc);
            gbc.weighty = defaultWeight;
            gbc.gridy++;
        }
    }

    public static Rectangle getCenteredKeepRatioRectangle(Container container, Rectangle rectangle) {
        return getCenteredKeepRatioRectangle(container.getWidth() - 1, container.getHeight() - 1, (int) rectangle.getWidth(), (int) rectangle.getHeight());
    }

    public static Rectangle getCenteredKeepRatioRectangle(int destWidth, int destHeight, int sourceWidth, int sourceHeight) {
        int xOffset = 0;
        int yOffset = 0;
        int width;
        int height;
        float hScale = (float) destWidth / sourceWidth;
        float vScale = (float) destHeight / sourceHeight;
        if (vScale < hScale) {
            width = (int) (sourceWidth * vScale);
            height = destHeight;
            xOffset = (destWidth - width) / 2;
        } else {
            height = (int) (sourceHeight * hScale);
            width = destWidth;
            yOffset = (destHeight - height) / 2;
        }
        return new Rectangle(xOffset, yOffset, width, height);
    }

    public static void setMinimumSize(Component component, int minimumWidth, int minimumHeight) {
        Dimension dim = new Dimension(minimumWidth, minimumHeight);
        component.setMinimumSize(dim);
        component.setPreferredSize(dim);
    }

    public static void setFixedSize(Component component, int minimumWidth, int minimumHeight) {
        Dimension dim = new Dimension(minimumWidth, minimumHeight);
        component.setMinimumSize(dim);
        component.setPreferredSize(dim);
        component.setMaximumSize(dim);
    }

    public static Component addBorder(Component comp, int top, int left, int bottom, int right) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(comp, BorderLayout.CENTER);
        panel.setBorder(new EmptyBorder(top, left, bottom, right));
        return panel;
    }

    public static void fill(Container parent, Component child) {
        parent.setLayout(new BorderLayout());
        parent.add(child, BorderLayout.CENTER);
    }

    public static <T> Supplier<T> bordered(Supplier<T> supplier) {
        return () -> {
            T result = supplier.get();
            ((JComponent) result).setBorder(createBorder());
            return result;
        };
    }

    public static JPanel single(Component component) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        wrapper.add(component, gbc);
        return wrapper;
    }

    public static Component componentAtIfExists(Container container, int index) {
        if (index < 0 || index >= container.getComponentCount()) {
            return null;
        } else {
            return container.getComponent(index);
        }
    }

}
