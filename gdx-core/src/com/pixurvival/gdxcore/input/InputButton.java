package com.pixurvival.gdxcore.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Value
@AllArgsConstructor
public class InputButton {

	@Getter
	@AllArgsConstructor
	public enum Type {
		MOUSE("M"),
		KEYBOARD("K");

		private String code;
	}

	private Type type;
	private int code;

	public static InputButton fromCode(String code) {
		if (code.length() < 2) {
			return null;
		}
		String typeCode = code.substring(0, 1);
		Type type;
		switch (typeCode) {
		case "M":
			type = Type.MOUSE;
			break;
		case "K":
			type = Type.KEYBOARD;
			break;
		default:
			return null;
		}
		String intCode = code.substring(1);
		if (intCode.matches("\\d+")) {
			return new InputButton(type, Integer.parseInt(intCode));
		} else {
			return null;
		}
	}

	public boolean isPressed() {
		if (type == Type.MOUSE) {
			return Gdx.input.isButtonPressed(code);
		} else {
			return Gdx.input.isKeyPressed(code);
		}
	}

	public String toStringCode() {
		return type.getCode() + code;
	}

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
				return "Button " + code;
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
