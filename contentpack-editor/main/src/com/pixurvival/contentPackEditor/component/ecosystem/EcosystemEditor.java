package com.pixurvival.contentPackEditor.component.ecosystem;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.ecosystem.StructureSpawner;

public class EcosystemEditor extends RootElementEditor<Ecosystem> {

	private static final long serialVersionUID = 1L;

	private ListEditor<StructureSpawner> structureSpawnersEditor = new VerticalListEditor<>(StructureSpawnerEditor::new, StructureSpawner::new, VerticalListEditor.HORIZONTAL);

	public EcosystemEditor() {

		bind(structureSpawnersEditor, Ecosystem::getStructureSpawners, Ecosystem::setStructureSpawners);

		LayoutUtils.fill(this, structureSpawnersEditor);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		((StructureSpawnerEditor) structureSpawnersEditor.getEditorForValidation()).setItems(event.getContentPack());
	}
}
