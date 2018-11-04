package com.pixurvival.contentPackEditor.component;

import java.awt.BorderLayout;

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

		setLayout(new BorderLayout(10, 0));
		add(list, BorderLayout.WEST);
		add(animationEditor, BorderLayout.CENTER);
	}

	@Override
	protected void valueChanged() {
		list.setMap(getValue().getAnimations());
	}
}
