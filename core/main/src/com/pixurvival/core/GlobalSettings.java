package com.pixurvival.core;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

public class GlobalSettings {

	@Getter
	@Setter
	private static File saveDirectory = new File("D:\\pixurvival_saves\\worldSaves");
}
