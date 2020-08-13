package com.pixurvival.gdxcore.util;

import com.pixurvival.core.util.CommonMainArgs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientMainArgs extends CommonMainArgs {

	private boolean zoomEnabled = false;
	private boolean redirectErrorToFile = true;
	private boolean mute = false;
	private String language = null;
}
