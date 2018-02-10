package com.pixurvival.core.contentPack;

public class ContentPackReadException extends ContentPackException {

	private static final long serialVersionUID = 1L;

	public ContentPackReadException(String message) {
		super(message);
	}

	public ContentPackReadException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContentPackReadException(Throwable cause) {
		super(cause);
	}
}
