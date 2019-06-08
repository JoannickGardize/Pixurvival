package com.pixurvival.gdxcore.input;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

import lombok.Value;

@Value
public class InputButton {

	public enum Type {
		MOUSE,
		KEYBOARD
	}

	private Type type;
	private int code;

	@Override
	public String toString() {
		if (type == Type.MOUSE) {
			switch (code) {
			case Buttons.LEFT:
				return "M1";
			case Buttons.RIGHT:
				return "M2";
			case Buttons.MIDDLE:
				return "M3";
			case Buttons.FORWARD:
				return "Wheel down";
			case Buttons.BACK:
				return "Wheel up";
			default:
				return "Unknown button";
			}
		} else {
			return Keys.toString(code);
		}
	}

	public static InputButton keyboard(int code) {
		return new InputButton(Type.KEYBOARD, code);
	}

	public static InputButton mouse(int code) {
		return new InputButton(Type.MOUSE, code);
	}
}
