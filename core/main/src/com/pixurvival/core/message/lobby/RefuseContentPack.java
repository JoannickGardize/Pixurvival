package com.pixurvival.core.message.lobby;

import com.pixurvival.core.contentPack.ContentPackIdentifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefuseContentPack implements LobbyRequest {

	private ContentPackIdentifier identifier;
}
