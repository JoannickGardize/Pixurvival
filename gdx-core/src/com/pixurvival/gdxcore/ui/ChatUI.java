package com.pixurvival.gdxcore.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.chat.ChatListener;
import com.pixurvival.core.message.playerRequest.ChatRequest;
import com.pixurvival.gdxcore.PixurvivalGame;

public class ChatUI extends UIWindow implements ChatListener {

	private ChatHistory chatHistory = new ChatHistory(30);
	private TextField inputArea;
	private ScrollPane displayAreScrollPane;

	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public ChatUI() {
		super("chat");
		displayAreScrollPane = new ScrollPane(chatHistory, PixurvivalGame.getSkin());

		inputArea = new TextField("test", PixurvivalGame.getSkin());
		inputArea.addCaptureListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					// chatHistory.push(new ChatEntry(inputArea.getText()));
					// inputArea.setText("");
					// validate();
					// displayAreScrollPane.setScrollPercentY(1);
					PixurvivalGame.getClient().sendAction(new ChatRequest(inputArea.getText()));
					inputArea.setText("");
				} else if (keycode == Keys.ESCAPE) {
					getStage().unfocusAll();
				}
				return true;
			}
		});

		add(displayAreScrollPane).expand().fill();
		row();
		add(inputArea).fill();
		pack();
	}

	@Override
	public void received(ChatEntry chatEntry) {
		StringBuilder sb = new StringBuilder("[");
		sb.append(dateFormat.format(new Date(chatEntry.getDateMillis())));
		sb.append("] ");
		sb.append(chatEntry.getSender().getName());
		sb.append(" : ");
		sb.append(chatEntry.getText());
		chatHistory.push(new ChatTextEntry(sb.toString()));
		inputArea.setText("");
		validate();
		displayAreScrollPane.setScrollPercentY(1);
	}

}
