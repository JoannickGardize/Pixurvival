package com.pixurvival.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackDenpendencyException;
import com.pixurvival.core.contentPack.ContentPackFileInfo;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.message.ContentPackPart;
import com.pixurvival.core.message.RequestContentPacks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ContentPackUploadManager extends Thread implements ServerGameListener {

	@Getter
	@AllArgsConstructor
	public static class RequestEntry {
		private PlayerConnection connection;
		private RequestContentPacks request;
	}

	private @NonNull ServerGame game;
	private ContentPackIdentifier[] dependencyList;
	private @Setter boolean running = true;

	private BlockingQueue<RequestEntry> requestQueue = new LinkedBlockingQueue<>();

	@Override
	public void playerLoggedIn(PlayerConnection playerConnection) {
		if (dependencyList != null) {
			playerConnection.sendTCP(new RequestContentPacks(dependencyList));
		}
	}

	public void setSelectedContentPack(ContentPack selectedContentPack) {
		try {
			List<ContentPackFileInfo> result = game.getContentPacksContext()
					.resolveDependencies(selectedContentPack.getInfo());
			dependencyList = new ContentPackIdentifier[result.size()];
			for (int i = 0; i < dependencyList.length; i++) {
				dependencyList[i] = new ContentPackIdentifier(result.get(i));
			}
			RequestContentPacks request = new RequestContentPacks(dependencyList);
			game.foreachPlayers(p -> p.sendTCP(request));
		} catch (ContentPackDenpendencyException e) {
			e.printStackTrace();
		}
	}

	public void sendContentPacks(PlayerConnection connection, RequestContentPacks request) {
		try {
			requestQueue.put(new RequestEntry(connection, request));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (running) {
				RequestEntry requestEntry = requestQueue.take();
				for (ContentPackIdentifier identifier : requestEntry.getRequest().getIdentifiers()) {
					File file = game.getContentPacksContext().fileOf(identifier);
					if (file == null) {
						Log.warn("Client requested unknown content pack : " + identifier);
						continue;
					}
					try (InputStream stream = new BufferedInputStream(new FileInputStream(file))) {

						int byteRead;
						int numberOfPart = (int) Math.ceil((double) file.length() / ContentPackPart.MAX_PART_LENGTH);
						for (int i = 0; i < numberOfPart; i++) {
							ContentPackPart part = new ContentPackPart();
							part.setIdentifier(identifier);
							part.setPartNumber(i);
							part.setNumberOfPart(numberOfPart);
							part.setData(new byte[ContentPackPart.MAX_PART_LENGTH]);
							byteRead = stream.read(part.getData());
							part.setLength(byteRead);
							requestEntry.getConnection().sendTCP(part);
						}
					} catch (IOException e) {
						Log.error("Error when trying to send content pack.", e);
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
