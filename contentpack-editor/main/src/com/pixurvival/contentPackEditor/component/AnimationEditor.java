package com.pixurvival.contentPackEditor.component;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.util.Bounds;
import com.pixurvival.contentPackEditor.component.util.DoubleInput;
import com.pixurvival.contentPackEditor.component.util.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class AnimationEditor extends ElementEditor<Animation> {

	private static final long serialVersionUID = 1L;

	private ListEditor<Frame> frameList = new ListEditor<>(() -> new FrameEditor(), () -> new Frame());
	private JTabbedPane previewTabs = new JTabbedPane();
	private AnimationPreview animationPreview = new AnimationPreview();
	private SpriteSheetPreview spriteSheetPreview = new SpriteSheetPreview();
	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<SpriteSheet>(LayoutUtils.getSpriteSheetIconProvider());

	public AnimationEditor() {
		EventManager.getInstance().register(this);

		JPanel previewPanel = new JPanel(new BorderLayout());
		previewPanel.setBorder(LayoutUtils.createGroupBorder("generic.preview"));
		previewPanel.add(spriteSheetChooser, BorderLayout.NORTH);
		previewPanel.add(previewTabs);
		previewTabs.add(TranslationService.getInstance().getString("generic.image"), spriteSheetPreview);
		previewTabs.add(TranslationService.getInstance().getString("generic.animation"), animationPreview);
		DoubleInput frameDurationInput = new DoubleInput();
		frameDurationInput.setValueBounds(Bounds.minBounds(Animation.MIN_FRAME_DURATION));
		frameList.setBorder(LayoutUtils.createGroupBorder("animationTemplateEditor.frames"));

		spriteSheetChooser.addValueChangeListener(s -> {
			animationPreview.setSpriteSheet(s);
			spriteSheetPreview.setSpriteSheet(s);
		});
		spriteSheetPreview.addInteractionListener(o -> {
			if (o instanceof Frame) {
				frameList.add((Frame) o);
			}
		});
		addSubValue(frameList, p -> frameList.setValue(p.getFrames()), (p, c) -> p.setFrames(c));
		addSubValue(frameDurationInput, p -> frameDurationInput.setValue(p.getFrameDuration()), (p, c) -> p.setFrameDuration(c));

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

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		spriteSheetChooser.setItems(event.getContentPack().getSpriteSheets());
	}

	@Override
	protected void valueChanged() {
		animationPreview.setAnimation(getValue());
	}
}
