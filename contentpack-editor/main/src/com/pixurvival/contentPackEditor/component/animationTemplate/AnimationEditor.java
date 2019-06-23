package com.pixurvival.contentPackEditor.component.animationTemplate;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.AnimationPreview;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetChooserPreviewTabs;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetPreview.ClickEvent;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.FrameEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.Frame;

public class AnimationEditor extends ElementEditor<Animation> {

	private static final long serialVersionUID = 1L;

	private VerticalListEditor<Frame> frameList = new VerticalListEditor<>(() -> new FrameEditor(), Frame::new);
	private AnimationPreview animationPreview = new AnimationPreview();
	private SpriteSheetChooserPreviewTabs previewPanel = new SpriteSheetChooserPreviewTabs();

	public AnimationEditor() {
		EventManager.getInstance().register(this);

		previewPanel.addTab(TranslationService.getInstance().getString("generic.animation"), animationPreview);
		TimeInput frameDurationInput = new TimeInput(Bounds.min(Animation.MIN_FRAME_DURATION));
		frameList.setBorder(LayoutUtils.createGroupBorder("animationTemplateEditor.frames"));

		previewPanel.getSpriteSheetChooser().addValueChangeListener(s -> {
			animationPreview.setSpriteSheet(s);
		});

		previewPanel.getSpriteSheetPreview().addInteractionListener(o -> {
			if (o instanceof ClickEvent) {
				ClickEvent clickEvent = (ClickEvent) o;
				frameList.add(new Frame(clickEvent.getSpriteX(), clickEvent.getSpriteY()));
			}
		});
		bind(frameList, Animation::getFrames, Animation::setFrames);
		bind(frameDurationInput, Animation::getFrameDuration, Animation::setFrameDuration);

		setLayout(new BorderLayout());
		JPanel editionPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.insets.top = 5;
		LayoutUtils.addHorizontalLabelledItem(editionPanel, "animationTemplateEditor.frameDuration", frameDurationInput, gbc);
		gbc.gridwidth = 2;
		gbc.weighty = 1;
		gbc.weightx = 1;
		editionPanel.add(frameList, gbc);

		add(editionPanel, BorderLayout.WEST);
		add(previewPanel, BorderLayout.CENTER);
	}

	@Override
	public boolean isValueValid(Animation value) {
		return super.isValueValid(value) && value.getFrames() != null && !value.getFrames().isEmpty();
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		animationPreview.setAnimation(getValue());
	}
}
