package com.pixurvival.gdxcore.notificationpush;

public interface NotificationPushPlug {

    void connect();

    void push(Notification notification);

    void update();

    void disconnect();

    String getUsername();
}
