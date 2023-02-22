package com.pixurvival.server;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientMessage {

    private PlayerConnection connection;
    private Object object;
}
