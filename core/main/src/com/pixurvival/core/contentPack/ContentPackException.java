package com.pixurvival.core.contentPack;

public class ContentPackException extends Exception {

	private static final long serialVersionUID = 1L;

	public ContentPackException(String message) {
		super(message);
	}

	public ContentPackException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContentPackException(Throwable cause) {
		super(cause);
	}
}
