package com.pixurvival.contentPackEditor.component.animationTemplate;

import java.awt.BorderLayout;

import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;

public class AnimationTemplateEditor extends RootElementEditor<AnimationTemplate> {

	private static final long serialVersionUID = 1L;

	private AnimationList list = new AnimationList();
	private AnimationEditor animationEditor = new AnimationEditor();

	public AnimationTemplateEditor() {

		animationEditor.setVisible(false);

		list.addListChangedListener(map -> {
			getValue().setAnimations(map);
			notifyValueChanged();
		});
		list.addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (list.getSelectedValue() == null) {
				animationEditor.setVisible(false);
			} else {
				animationEditor.setValue(list.getSelectedValue());
				animationEditor.setVisible(true);
			}
		});
		animationEditor.addValueChangeListener(l -> {
			list.repaint();
			notifyValueChanged();
		});

		setLayout(new BorderLayout(10, 0));
		add(list, BorderLayout.WEST);
		add(animationEditor, BorderLayout.CENTER);
	}

	@Override
	public boolean isValueValid(AnimationTemplate value) {
		if (value.getAnimations().isEmpty()) {
			return false;
		}
		for (Animation animation : value.getAnimations().values()) {
			if (!animationEditor.isValueValid(animation)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		list.setMap(getValue().getAnimations());
		list.repaint();
	}
}
