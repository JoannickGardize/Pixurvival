package com.pixurvival.contentPackEditor.component.tile;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.AnimationPreview;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.FrameEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.ContentPackConstantChangedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class TileEditor extends RootElementEditor<Tile> {

	private static final long serialVersionUID = 1L;

	private AnimationPreview imagePreview = new AnimationPreview();

	public TileEditor() {

		// Construction
		ElementChooserButton<ResourceEntry> imageField = new ElementChooserButton<>(ResourcesService.getInstance().getResourcesSupplier());
		EventManager.getInstance().register(this);
		VerticalListEditor<Frame> frameList = new VerticalListEditor<>(FrameEditor::new, Frame::new, VerticalListEditor.HORIZONTAL);
		BooleanCheckBox solidCheckBox = new BooleanCheckBox();
		FloatInput velocityFactorInput = new FloatInput(Bounds.positive());
		imagePreview.setAnimation(new Animation());
		SpriteSheet tileSpriteSheet = new SpriteSheet();
		tileSpriteSheet.setWidth(GameConstants.PIXEL_PER_UNIT);
		tileSpriteSheet.setHeight(GameConstants.PIXEL_PER_UNIT);
		imagePreview.setSpriteSheet(tileSpriteSheet);
		LayoutUtils.setMinimumSize(imagePreview, 32, 32);

		// Binding
		bind(imageField, v -> v.getImage() == null ? null : ResourcesService.getInstance().getResource(v.getImage()), (v, f) -> v.setImage(f == null ? null : f.getName()));
		bind(frameList, Tile::getFrames, Tile::setFrames);
		bind(solidCheckBox, Tile::isSolid, Tile::setSolid);
		bind(velocityFactorInput, Tile::getVelocityFactor, Tile::setVelocityFactor);

		// Layouting
		setLayout(new BorderLayout(10, 5));

		JPanel leftPanel = LayoutUtils.createVerticalBox(10, 2, imagePreview, LayoutUtils.labelled("generic.image", imageField), frameList);
		leftPanel.setBorder(LayoutUtils.createGroupBorder("generic.image"));
		add(leftPanel, BorderLayout.WEST);
		JPanel centerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(centerPanel, "generic.solid", solidCheckBox, gbc);
		LayoutUtils.addHorizontalLabelledItem(centerPanel, "tileEditor.velocityFactor", velocityFactorInput, gbc);
		add(centerPanel, BorderLayout.CENTER);

	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		imagePreview.getAnimation().setFrames(getValue().getFrames());
		imagePreview.getSpriteSheet().setImage(getValue().getImage());
	}

	@EventListener
	public void contentPackConstantChangedEvent(ContentPackConstantChangedEvent event) {
		imagePreview.getAnimation().setFrameDuration(event.getConstants().getTileAnimationSpeed());
	}
}
