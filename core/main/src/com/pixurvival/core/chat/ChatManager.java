package com.pixurvival.core.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatManager {

	private List<ChatListener> listeners = new ArrayList<>();

	public void addListener(ChatListener listener) {
		listeners.add(listener);
	}

	public void received(ChatEntry chatEntry) {
		listeners.forEach(l -> l.received(chatEntry));
	}
}
