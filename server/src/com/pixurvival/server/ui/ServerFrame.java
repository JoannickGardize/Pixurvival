package com.pixurvival.server.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.util.ArgsUtils;
import com.pixurvival.server.GameSession;
import com.pixurvival.server.PixurvivalServer;
import com.pixurvival.server.PlayerConnection;
import com.pixurvival.server.ServerGameListener;
import com.pixurvival.server.console.CommandMultiplexer;
import com.pixurvival.server.console.ServerCommands;
import com.pixurvival.server.lobby.LobbySession;
import com.pixurvival.server.util.ServerMainArgs;

public class ServerFrame extends JPanel implements ServerGameListener {

	private static final long serialVersionUID = 1L;

	private JTextPane outputTextPane = new JTextPane();
	private JTextField inputTextField = new JTextField();

	public ServerFrame(ServerMainArgs mainArgs) {
		outputTextPane.setEditable(false);
		System.setOut(new PrintStream(new TextPaneOutputStream(outputTextPane, Color.BLACK), true));
		System.setErr(new PrintStream(new TextPaneOutputStream(outputTextPane, Color.RED), true));
		PixurvivalServer game = new PixurvivalServer(mainArgs);
		game.addListener(this);
		CommandMultiplexer commands = new ServerCommands(game);
		setLayout(new BorderLayout());
		inputTextField.addActionListener(event -> {

			try {
				commands.process(CommandArgsUtils.splitArgs(inputTextField.getText()));
				inputTextField.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		JScrollPane outputScrollPane = new JScrollPane(outputTextPane);
		outputTextPane.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				outputScrollPane.getVerticalScrollBar().setValue(outputScrollPane.getVerticalScrollBar().getMaximum());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		add(new InfosPanel(game), BorderLayout.NORTH);
		add(outputScrollPane, BorderLayout.CENTER);
		add(inputTextField, BorderLayout.SOUTH);
	}

	@Override
	public void playerRejoined(PlayerConnection playerConnection) {
		Log.info("New player connected : " + playerConnection);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Pixurvival Server");
		frame.setContentPane(new ServerFrame(ArgsUtils.readArgs(args, ServerMainArgs.class)));
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	@Override
	public void lobbyStarted(LobbySession lobbySession) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameStarted(GameSession gameSession) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameEnded(EndGameData data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerLoggedIn(PlayerConnection playerConnection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected(PlayerConnection playerConnection) {
		// TODO Auto-generated method stub

	}

}
