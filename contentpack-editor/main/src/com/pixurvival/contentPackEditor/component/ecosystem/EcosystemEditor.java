package com.pixurvival.contentPackEditor.component.ecosystem;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.ecosystem.StructureSpawner;

import javax.swing.*;
import java.awt.*;

public class EcosystemEditor extends RootElementEditor<Ecosystem> {

    private static final long serialVersionUID = 1L;

    private ListEditor<StructureSpawner> structureSpawnersEditor = new VerticalListEditor<>(StructureSpawnerEditor::new, StructureSpawner::new, VerticalListEditor.VERTICAL);
    private DarknessSpawnerEditor darknessSpawnerEditor = new DarknessSpawnerEditor();

    public EcosystemEditor() {
        super(Ecosystem.class);

        bind(structureSpawnersEditor, "structureSpawners");
        bind(darknessSpawnerEditor, "darknessSpawner");

        LayoutUtils.fill(this, structureSpawnersEditor);

        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);
        TranslationService t = TranslationService.getInstance();
        tabbedPane.addTab(t.getString("ecosystemEditor.structureSpawners"), structureSpawnersEditor);
        tabbedPane.addTab(t.getString("ecosystemEditor.darknessSpawner"), darknessSpawnerEditor);
    }

}
