package com.pixurvival.discordbot.util;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.pixurvival.discordbot.PixBot;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.Request;
import okhttp3.Response;

@UtilityClass
public class DiscordUtils {

	public static final String CONTENT_PACK_MANAGER_ROLE = "Content Pack Manager";

	public static final String PLAY_CHANNEL = "play-together";

	private static final @Getter Collection<Locale> localePriority = Collections.singleton(Locale.US);

	public static Attachment getAttachmentIfAny(Message message) {
		List<Attachment> attachments = message.getAttachments();
		if (attachments.isEmpty()) {
			return null;
		} else {
			return attachments.get(0);
		}
	}

	public static boolean hasRole(Member member, String role) {
		return member.getRoles().stream().anyMatch(r -> r.getName().equals(role));
	}

	public static void sendMessage(MessageReceivedEvent event, String message) {
		event.getChannel().sendMessage(message).queue();
	}

	public static void sendEmbed(MessageReceivedEvent event, EmbedBuilder builder) {
		builder.setColor(Color.GRAY);
		event.getChannel().sendMessage(builder.build()).queue();
	}

	public static String getMyIp() {
		try (Response response = PixBot.getJda().getHttpClient().newCall(new Request.Builder().url("https://api.my-ip.io/ip").build()).execute()) {
			return response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
			return "I don't know";
		}
	}
}
