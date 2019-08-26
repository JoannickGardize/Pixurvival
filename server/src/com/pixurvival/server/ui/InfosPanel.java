package com.pixurvival.server.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.pixurvival.server.NetworkStats;
import com.pixurvival.server.ServerGame;

public class InfosPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public InfosPanel(ServerGame game) {
		InfoLabel entityAvgLabel = new InfoLabel("Entity avg ", "0");
		InfoLabel entityMaxLabel = new InfoLabel("Entity max ", "0");
		InfoLabel chunkAvgLabel = new InfoLabel("Chunk avg ", "0");
		InfoLabel chunkMaxLabel = new InfoLabel("Chunk max ", "0");
		NetworkStats netStats = new NetworkStats();
		game.addNetworkListener(netStats);
		netStats.setChangedAction(() -> SwingUtilities.invokeLater(() -> {
			entityAvgLabel.setInfoValue(Integer.toString(netStats.getEntityLengthAverage().getAverage()));
			entityMaxLabel.setInfoValue(Integer.toString(netStats.getEntityLengthAverage().getMaximum()));
			chunkAvgLabel.setInfoValue(Integer.toString(netStats.getCompressedChunkLengthAverage().getAverage()));
			chunkMaxLabel.setInfoValue(Integer.toString(netStats.getCompressedChunkLengthAverage().getMaximum()));
		}));
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(entityAvgLabel, gbc);
		gbc.gridy = 1;
		add(chunkAvgLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(entityMaxLabel, gbc);
		gbc.gridy = 1;
		add(chunkMaxLabel, gbc);
	}
}
