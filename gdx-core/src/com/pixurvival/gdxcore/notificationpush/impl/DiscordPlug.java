package com.pixurvival.gdxcore.notificationpush.impl;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.gdxcore.notificationpush.Notification;
import com.pixurvival.gdxcore.notificationpush.NotificationPushPlug;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

@NoArgsConstructor(access = AccessLevel.NONE)
public class DiscordPlug implements NotificationPushPlug {

	private static final @Getter DiscordPlug instance = new DiscordPlug();

	private @Getter String username = null;

	@Override
	public void connect() {
		try {
			DiscordRPC.discordInitialize("742825259190714439", new DiscordEventHandlers.Builder().setReadyEventHandler(user -> username = user.username).build(), true);
		} catch (Exception e) {
			Log.warn("Exception on initializing Discord RPC : " + e.getMessage());
		}
	}

	@Override
	public void update() {
		DiscordRPC.discordRunCallbacks();
	}

	@Override
	public void push(Notification notification) {
		DiscordRichPresence.Builder rich = new DiscordRichPresence.Builder(notification.getStatus()).setDetails(notification.getDetails()).setBigImage(notification.getImage(), "");
		if (notification.getParty() != null) {
			rich.setParty("Party", notification.getParty().getActual(), notification.getParty().getMaximum());
		}
		if (notification.getStartTime() != null) {
			rich.setStartTimestamps(notification.getStartTime());
		}
		DiscordRPC.discordUpdatePresence(rich.build());
	}

	@Override
	public void disconnect() {
		DiscordRPC.discordShutdown();
	}

}
