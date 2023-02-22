package com.pixurvival.gdxcore.notificationpush;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Notification {

    private String status;
    private String details;
    private String image = "pixurvival_cover";
    private Party party;
    private Long startTime;

}
