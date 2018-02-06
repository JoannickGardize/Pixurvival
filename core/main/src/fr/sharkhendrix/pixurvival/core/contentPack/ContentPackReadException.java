package fr.sharkhendrix.pixurvival.core.contentPack;

public class ContentPackReadException extends Exception {

	private static final long serialVersionUID = 8210060960511026694L;

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
