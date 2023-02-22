package com.pixurvival.contentPackEditor.component.structure;

import javax.swing.*;

public abstract class StructureSpecificPartPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Bind to the itemEditor all the specific attributes.
     *
     * @param itemEditor
     */
    public abstract void bindTo(StructureEditor structureEditor);
}
