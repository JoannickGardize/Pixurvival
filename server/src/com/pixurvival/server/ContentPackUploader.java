package com.pixurvival.server;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.message.ContentPackPart;
import lombok.*;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
                Log.warn("ContentPackUploader interrupted.", e);
                Thread.currentThread().interrupt();
                return;
            }
            File file;
            try {
                file = game.getContentPackContext().fileOf(sendingEntry.getIdentifier());
            } catch (ContentPackException e1) {
                Log.error("Error when trying to send content pack.", e1);
                continue;
            }
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
