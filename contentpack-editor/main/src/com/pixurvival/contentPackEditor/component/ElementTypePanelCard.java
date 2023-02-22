package com.pixurvival.contentPackEditor.component;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ElementSelectedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;

import javax.swing.*;
import java.awt.*;

public class ElementTypePanelCard extends JPanel {

    public static final String NONE_CARD = "NONE";

    private static final long serialVersionUID = 1L;

    public ElementTypePanelCard() {
        setLayout(new CardLayout());
        add(new JPanel(), NONE_CARD);
        for (ElementType elementType : ElementType.values()) {
            add(ContentPackEditionService.getInstance().editorOf(elementType), elementType.name());
        }
        ((CardLayout) getLayout()).show(this, NONE_CARD);
        EventManager.getInstance().register(this);
    }

    @SuppressWarnings("unchecked")
    @EventListener
    public void elementSelected(ElementSelectedEvent event) {
        ElementType type = ElementType.of(event.getElement());
        ((CardLayout) getLayout()).show(this, type.name());
        ContentPackEditionService.getInstance().editorOf(type).setValue(event.getElement());
    }

    @EventListener
    public void contentPackLoaded(ContentPackLoadedEvent event) {
        ((CardLayout) getLayout()).show(this, NONE_CARD);
    }
}
