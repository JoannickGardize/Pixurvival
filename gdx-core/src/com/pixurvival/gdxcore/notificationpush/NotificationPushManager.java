package com.pixurvival.gdxcore.notificationpush;

import com.pixurvival.gdxcore.notificationpush.impl.DiscordPlug;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class NotificationPushManager {

    private static final @Getter NotificationPushManager instance = new NotificationPushManager();

    private List<NotificationPushPlug> plugs = new ArrayList<>();

    private NotificationPushManager() {
        plugs.add(DiscordPlug.getInstance());
    }

    public void start() {
        plugs.forEach(NotificationPushPlug::connect);
    }

    public void update() {
        plugs.forEach(NotificationPushPlug::update);
    }

    public void push(Notification notification) {
        plugs.forEach(p -> p.push(notification));
    }

    public void stop() {
        plugs.forEach(NotificationPushPlug::disconnect);
    }

    public String getUsername() {
        for (NotificationPushPlug plug : plugs) {
            if (plug.getUsername() != null) {
                return plug.getUsername();
            }
        }
        return null;
    }
}
