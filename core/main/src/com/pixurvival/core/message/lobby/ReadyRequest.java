package com.pixurvival.core.message.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadyRequest extends LobbyRequest {

	private boolean ready;
	private int modCount;
}
