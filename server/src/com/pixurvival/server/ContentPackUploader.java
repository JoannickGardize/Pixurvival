package com.pixurvival.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.message.ContentPackPart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ContentPackUploader extends Thread {

	@Getter
	@AllArgsConstructor
	public static class SendingEntry {
		private PlayerConnection connection;
		private ContentPackIdentifier identifier;
	}

	private @NonNull PixurvivalServer game;
	private @Setter boolean running = true;

	private BlockingQueue<SendingEntry> sendingQueue = new LinkedBlockingQueue<>();

	public void sendContentPack(PlayerConnection connection, ContentPackIdentifier identifier) {
		try {
			sendingQueue.put(new SendingEntry(connection, identifier));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (running) {
			SendingEntry sendingEntry;
			try {
				sendingEntry = sendingQueue.take();
			} catch (InterruptedException e) {
				System.out.println(e);
				Thread.currentThread().interrupt();
				return;
			}
			File file = game.getContentPackSerialization().fileOf(sendingEntry.getIdentifier());
			try (InputStream stream = new BufferedInputStream(new FileInputStream(file))) {

				int byteRead;
				int numberOfPart = (int) Math.ceil((float) file.length() / ContentPackPart.MAX_PART_LENGTH);
				for (int i = 0; i < numberOfPart; i++) {
					ContentPackPart part = new ContentPackPart();
					part.setIdentifier(sendingEntry.getIdentifier());
					part.setPartNumber(i);
					part.setNumberOfPart(numberOfPart);
					part.setData(new byte[ContentPackPart.MAX_PART_LENGTH]);
					byteRead = stream.read(part.getData());
					part.setLength(byteRead);
					sendingEntry.getConnection().sendTCP(part);
				}
			} catch (IOException e) {
				Log.error("Error when trying to send content pack.", e);
			}
		}

	}
}