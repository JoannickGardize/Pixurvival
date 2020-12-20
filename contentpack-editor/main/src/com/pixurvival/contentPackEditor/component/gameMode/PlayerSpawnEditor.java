package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.PercentInput;
import com.pixurvival.contentPackEditor.component.valueComponent.Vector2Editor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.gameMode.spawn.AutoSquarePlayerSpawn;
import com.pixurvival.core.contentPack.gameMode.spawn.PlayerSpawn;
import com.pixurvival.core.contentPack.gameMode.spawn.StaticPlayerSpawn;
import com.pixurvival.core.util.Vector2;

public class PlayerSpawnEditor extends InstanceChangingElementEditor<PlayerSpawn> {

	public PlayerSpawnEditor() {
		super(PlayerSpawn.class, "playerSpawnType");

		setLayout(new BorderLayout(5, 5));

		add(LayoutUtils.single(LayoutUtils.labelled("generic.type", getTypeChooser())), BorderLayout.NORTH);
		add(getSpecificPartPanel(), BorderLayout.CENTER);
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> result = new ArrayList<>();
		result.add(new ClassEntry(AutoSquarePlayerSpawn.class, () -> {
			IntegerInput sizeInput = new IntegerInput();
			PercentInput minFreeSpaceInput = new PercentInput();
			PercentInput maxFreeSpaceInput = new PercentInput();

			bind(sizeInput, "size", AutoSquarePlayerSpawn.class);
			bind(minFreeSpaceInput, "minFreeSpace", AutoSquarePlayerSpawn.class);
			bind(maxFreeSpaceInput, "maxFreeSpace", AutoSquarePlayerSpawn.class);

			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
			LayoutUtils.addHorizontalLabelledItem(panel, "generic.size", sizeInput, gbc);
			LayoutUtils.addHorizontalLabelledItem(panel, "gameMode.playerSpawn.minFreeSpace", minFreeSpaceInput, gbc);
			LayoutUtils.addHorizontalLabelledItem(panel, "gameMode.playerSpawn.maxFreeSpace", maxFreeSpaceInput, gbc);

			return panel;
		}));
		result.add(new ClassEntry(StaticPlayerSpawn.class, () -> {
			ListEditor<Vector2> positionsEditor = new VerticalListEditor<>(Vector2Editor::new, Vector2::new, VerticalListEditor.HORIZONTAL);
			bind(positionsEditor, "positions", StaticPlayerSpawn.class);
			return positionsEditor;
		}));
		return result;
	}

	@Override
	protected void initialize(PlayerSpawn oldInstance, PlayerSpawn newInstance) {
	}

}
