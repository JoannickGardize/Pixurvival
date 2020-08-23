package com.pixurvival.gdxcore.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.chat.ChatListener;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.playerRequest.ChatRequest;
import com.pixurvival.gdxcore.PixurvivalGame;

public class ChatUI extends UIWindow implements ChatListener {

	private ChatHistory chatHistory = new ChatHistory(30);
	private TextField inputArea;
	private ScrollPane displayAreScrollPane;

	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private List<String> history = new ArrayList<>();
	private int currentHistoryIndex = -1;

	public ChatUI() {
		super("chat");
		displayAreScrollPane = new ScrollPane(chatHistory, PixurvivalGame.getSkin());
		displayAreScrollPane.setScrollingDisabled(true, false);

		inputArea = new TextField("", PixurvivalGame.getSkin());
		inputArea.addCaptureListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				switch (keycode) {
				case Keys.ENTER:
					history.add(inputArea.getText());
					PixurvivalGame.getClient().sendAction(new ChatRequest(inputArea.getText()));
					inputArea.setText("");
					getStage().unfocusAll();
					currentHistoryIndex = -1;
					break;
				case Keys.ESCAPE:
					getStage().unfocusAll();
					currentHistoryIndex = -1;
					break;
				case Keys.TAB:
					PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
					inputArea.setText(myPlayer.getWorld().getCommandManager().autocomplete(myPlayer, inputArea.getText()));
					inputArea.setCursorPosition(inputArea.getText().length());
					currentHistoryIndex = -1;
					break;
				case Keys.UP:
					if (currentHistoryIndex == 0 || history.isEmpty()) {
						break;
					} else if (currentHistoryIndex == -1) {
						currentHistoryIndex = history.size() - 1;
					} else {
						currentHistoryIndex--;
					}
					inputArea.setText(history.get(currentHistoryIndex));
					break;
				case Keys.DOWN:
					if (currentHistoryIndex == history.size() - 1 || history.isEmpty()) {
						break;
					}
					currentHistoryIndex++;
					inputArea.setText(history.get(currentHistoryIndex));
					break;
				default:
					currentHistoryIndex = -1;
					break;
				}

				return true;
			}
		});

		add(displayAreScrollPane).expand().fill();
		row();
		add(inputArea).fill();
		pack();
	}

	public void focusTextInput() {
		getStage().setKeyboardFocus(inputArea);
		inputArea.getOnscreenKeyboard().show(true);
	}

	@Override
	public void received(ChatEntry chatEntry) {
		StringBuilder sb = new StringBuilder();
		if (chatEntry.getSender() instanceof PlayerEntity) {
			if (((PlayerEntity) chatEntry.getSender()).getTeam() == PixurvivalGame.getClient().getMyPlayer().getTeam()) {
				sb.append("[GREEN]");
			} else {
				sb.append("[RED]");
			}
		} else {
			sb.append("[BLUE]");
		}
		sb.append("[[");
		sb.append(dateFormat.format(new Date(chatEntry.getDateMillis())));
		sb.append("] ");
		sb.append(chatEntry.getSender().getName());
		sb.append("[] : ");
		sb.append(chatEntry.getText());
		chatHistory.push(new ChatTextEntry(sb.toString()));
		validate();
		displayAreScrollPane.setScrollPercentY(1);
	}

}
