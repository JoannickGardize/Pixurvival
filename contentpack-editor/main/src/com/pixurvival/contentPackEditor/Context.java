package com.pixurvival.contentPackEditor;

import java.util.ResourceBundle;

import javax.swing.JFrame;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class Context {

	private static @Getter Context instance = new Context();

	private @Getter ResourceBundle bundle = ResourceBundle.getBundle("translation/translation");
	private @Getter @Setter(AccessLevel.PACKAGE) JFrame frame;

	private Context() {

	}
}
