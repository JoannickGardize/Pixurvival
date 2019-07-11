package com.pixurvival.contentPackEditor.component.ecosystem;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.core.contentPack.ecosystem.DarknessSpawner;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.ecosystem.StructureSpawner;

public class EcosystemEditor extends RootElementEditor<Ecosystem> {

	private static final long serialVersionUID = 1L;

	private ListEditor<StructureSpawner> structureSpawnersEditor = new VerticalListEditor<>(StructureSpawnerEditor::new, StructureSpawner::new, VerticalListEditor.HORIZONTAL);
	private DarknessSpawnerEditor<DarknessSpawner> darknessSpawnerEditor = new DarknessSpawnerEditor<>();

	public EcosystemEditor() {

		bind(structureSpawnersEditor, Ecosystem::getStructureSpawners, Ecosystem::setStructureSpawners);
		bind(darknessSpawnerEditor, Ecosystem::getDarknessSpawner, Ecosystem::setDarknessSpawner);

		LayoutUtils.fill(this, structureSpawnersEditor);

		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
		TranslationService t = TranslationService.getInstance();
		tabbedPane.addTab(t.getString("ecosystemEditor.structureSpawners"), structureSpawnersEditor);
		tabbedPane.addTab(t.getString("ecosystemEditor.darknessSpawner"), darknessSpawnerEditor);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		((StructureSpawnerEditor) structureSpawnersEditor.getEditorForValidation()).setItems(event.getContentPack());
	}
}
