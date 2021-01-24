package com.pixurvival.core.chat;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.WorldKryo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatEntry {

	private static final byte PLAYER_TYPE = 0;
	private static final byte WORLD_TYPE = 1;

	private long dateMillis;
	private ChatSender sender;
	private String text;

	public ChatEntry(ChatSender sender, String text) {
		this(System.currentTimeMillis(), sender, text);
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<ChatEntry> {

		@Override
		public void write(Kryo kryo, Output output, ChatEntry object) {
			output.writeLong(object.dateMillis);
			if (object.sender instanceof PlayerEntity) {
				output.writeByte(PLAYER_TYPE);
				output.writeLong(((PlayerEntity) object.sender).getId());
			} else {
				output.writeByte(WORLD_TYPE);
			}
			output.writeString(object.text);
		}

		@Override
		public ChatEntry read(Kryo kryo, Input input, Class<ChatEntry> type) {
			long dateMillis = input.readLong();
			ChatSender sender;
			World world = ((WorldKryo) kryo).getWorld();
			byte senderType = input.readByte();
			if (senderType == PLAYER_TYPE) {
				sender = world.getPlayerEntities().get(input.readLong());
			} else {
				sender = world;
			}
			return new ChatEntry(dateMillis, sender, input.readString());
		}

	}
}
