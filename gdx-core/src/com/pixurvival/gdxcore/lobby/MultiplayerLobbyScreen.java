package com.pixurvival.gdxcore.lobby;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.serialization.ContentPackValidityCheckResult;
import com.pixurvival.core.contentPack.summary.GameModeSummary;
import com.pixurvival.core.message.lobby.CreateTeamRequest;
import com.pixurvival.core.message.lobby.LobbyData;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.core.message.lobby.LobbyPlayer;
import com.pixurvival.core.message.lobby.LobbyServerMessage;
import com.pixurvival.core.message.lobby.LobbyTeam;
import com.pixurvival.core.message.lobby.ReadyRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.menu.InputWindow;
import com.pixurvival.gdxcore.menu.MainMenuScreen;
import com.pixurvival.gdxcore.menu.MessageWindow;
import com.pixurvival.gdxcore.menu.QuestionWindow;
import com.pixurvival.gdxcore.notificationpush.Notification;
import com.pixurvival.gdxcore.notificationpush.NotificationPushManager;
import com.pixurvival.gdxcore.notificationpush.Party;
import com.pixurvival.gdxcore.ui.EndGameUI;

public class MultiplayerLobbyScreen implements Screen {

	public static final int MAX_TEAM_PER_ROW = 4;

	private Stage stage;
	private GameModeChooser gameModeChooser;
	private Table teamsTable;
	private InputWindow teamNameWindow = new InputWindow("lobby.addTeam", "generic.add", name -> PixurvivalGame.getClient().send(new CreateTeamRequest(name)));
	private QuestionWindow questionWindow;
	private TextButton readyButton;
	private int modCount;
	private LobbyPlayer myPlayer;
	private EndGameUI endGameUI;
	private MessageWindow messageWindow = new MessageWindow("menu.multiplayer.message");

	@Override
	public void show() {
		Table mainGroup = new Table();
		mainGroup.setFillParent(true);

		gameModeChooser = new GameModeChooser();
		mainGroup.add(gameModeChooser).expandY().fill().pad(2);

		teamsTable = new Table();
		teamsTable.defaults().expand().fill().pad(2);
		mainGroup.add(teamsTable).expand().fill();

		mainGroup.row();

		TextButton addTeamButton = new TextButton(PixurvivalGame.getString("lobby.addTeam"), PixurvivalGame.getSkin());

		addTeamButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				teamNameWindow.setVisible("");
			}
		});

		readyButton = new TextButton(PixurvivalGame.getString("lobby.ready"), PixurvivalGame.getSkin());

		readyButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (myPlayer.isReady()) {
					PixurvivalGame.getClient().send(new ReadyRequest(false, modCount));
				} else {
					PixurvivalGame.getClient().send(new ReadyRequest(true, modCount));
				}
			}
		});

		TextButton backButton = new TextButton(PixurvivalGame.getString("generic.back"), PixurvivalGame.getSkin());
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PixurvivalGame.getClient().disconnectFromServer();
				PixurvivalGame.getInstance().disposeServer();
				PixurvivalGame.setScreen(MainMenuScreen.class);
			}
		});

		Table buttonGroup = new Table();
		buttonGroup.defaults().prefWidth(150);
		buttonGroup.add(backButton).padRight(20);
		buttonGroup.add(addTeamButton);
		buttonGroup.add(readyButton);
		mainGroup.add(buttonGroup).colspan(2).expandX().align(Align.center);

		teamNameWindow.setVisible(false);
		stage = new Stage(new ScreenViewport());
		stage.addActor(mainGroup);
		stage.addActor(teamNameWindow);
		messageWindow.getOkButton().setVisible(true);
		stage.addActor(messageWindow);
		Gdx.input.setInputProcessor(stage);
	}

	public void showEndGameUI(EndGameUI endGameUI) {
		if (this.endGameUI != null) {
			this.endGameUI.remove();
		}
		stage.addActor(endGameUI);
	}

	public void receivedLobbyMessage(LobbyMessage message) {
		if (message instanceof LobbyData) {
			setLobbyData((LobbyData) message);
		} else if (message instanceof LobbyServerMessage) {
			LobbyServerMessage serverMessage = (LobbyServerMessage) message;
			String messageContent = "Unknown message";
			GameModeSummary gameMode = gameModeChooser.getSelectedGameMode();
			if (gameMode == null) {
				return;
			}
			switch (serverMessage) {
			case NUMBER_OF_PLAYER:
				messageContent = PixurvivalGame.getString("menu.multiplayer.message.numberOfPlayer", gameMode.getTeamSizeInterval().getMin(), gameMode.getTeamSizeInterval().getMax());
				break;
			case NUMBER_OF_ROLE:
				messageContent = PixurvivalGame.getString("menu.multiplayer.message.numberOfRole");
				break;
			case NUMBER_OF_TEAM:
				messageContent = PixurvivalGame.getString("menu.multiplayer.message.numberOfTeam", gameMode.getTeamNumberInterval().getMin(), gameMode.getTeamNumberInterval().getMax());
				break;
			}
			messageWindow.getContentLabel().setText(messageContent);
			messageWindow.setVisible(true);
		}
	}

	private void setLobbyData(LobbyData lobbyData) {
		modCount = lobbyData.getModCount();
		myPlayer = lobbyData.getMyPlayer();
		if (myPlayer.isReady()) {
			readyButton.setText(PixurvivalGame.getString("lobby.notReady"));
		} else {
			readyButton.setText(PixurvivalGame.getString("lobby.ready"));
		}
		setTeamTable(lobbyData);
		gameModeChooser.setData(lobbyData);
		NotificationPushManager.getInstance().push(Notification.builder().status("In lobby").party(new Party(lobbyData.getPlayers().length, gameModeChooser.countLobbyMaxPlayer())).build());
	}

	private void setTeamTable(LobbyData lobbyData) {
		teamsTable.clear();
		int count = 0;
		for (LobbyTeam team : lobbyData.getPlayers()) {
			TeamPanel teamPanel;
			if (Objects.equals(lobbyData.getMyTeamName(), team.getName())) {
				teamPanel = new TeamPanel(true);
			} else {
				teamPanel = new TeamPanel(false);
			}
			teamPanel.setTeamName(team.getName());
			teamPanel.setPlayerList(lobbyData.getAvailableContentPacks()[lobbyData.getSelectedContentPackIndex()].getGameModeSummaries()[lobbyData.getSelectedGameModeIndex()].getRoleSummaries(),
					team.getMembers(), lobbyData.getMyPlayer().getPlayerName());
			teamsTable.add(teamPanel);
			count++;
			if (count >= MAX_TEAM_PER_ROW) {
				count = 0;
				teamsTable.row();
			}
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		teamNameWindow.update(stage.getViewport());
		if (questionWindow != null) {
			questionWindow.update(stage.getViewport());
		}
		if (endGameUI != null) {
			endGameUI.update(stage.getViewport());
		}
		messageWindow.update(stage.getViewport());
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		if (stage != null) {
			stage.dispose();
			stage = null;
		}
	}

	public void questionDownloadContentPack(ContentPackIdentifier identifier, ContentPackValidityCheckResult checkResult) {
		String messageKey = checkResult == ContentPackValidityCheckResult.NOT_FOUND ? "lobby.downloadContentPack.notFound" : "lobby.downloadContentPack.checksum";
		// TODO keep this instance
		questionWindow = new QuestionWindow("lobby.downloadContentPack.title", messageKey, () -> {
			PixurvivalGame.getClient().acceptContentPack(identifier);
			questionWindow.remove();
		}, () -> {
			PixurvivalGame.getClient().refuseContentPack(identifier);
			questionWindow.remove();
		});
		stage.addActor(questionWindow);
		questionWindow.update(stage.getViewport());
		questionWindow.setVisible(true);
	}
}
