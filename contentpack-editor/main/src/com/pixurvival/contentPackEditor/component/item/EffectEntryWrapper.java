package com.pixurvival.contentPackEditor.component.item;

import java.awt.BorderLayout;
import java.util.Collection;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueChangeListener;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.effect.Effect;

public class EffectEntryWrapper extends ElementEditor<Effect> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Effect> chooser = new ElementChooserButton<>();

	public EffectEntryWrapper() {
		chooser.addValueChangeListener(this::setValue);
		ContentPack pack = FileService.getInstance().getCurrentContentPack();
		if (pack != null) {
			chooser.setItems(pack.getEffects());
		}

		setLayout(new BorderLayout());
		add(chooser, BorderLayout.CENTER);
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		chooser.setValue(getValue());
	}

	@Override
	public boolean isValueValid(Effect value) {
		return chooser.isValueValid(value);
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<Effect> listener) {
		chooser.addValueChangeListener(listener);
	}

	public void setItems(Collection<Effect> items) {
		chooser.setItems(items);
	}
}
