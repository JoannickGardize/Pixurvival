package com.pixurvival.discordbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import javax.security.auth.login.LoginException;

import com.pixurvival.discordbot.command.HelpCommand;
import com.pixurvival.discordbot.command.MultiCommand;
import com.pixurvival.discordbot.command.PlayCommand;
import com.pixurvival.discordbot.command.contentpack.DetailsContentPackCommand;
import com.pixurvival.discordbot.command.contentpack.GiveContentPackCommand;
import com.pixurvival.discordbot.command.contentpack.ListContentPackCommand;
import com.pixurvival.discordbot.command.contentpack.RemoveContentPackCommand;
import com.pixurvival.discordbot.command.contentpack.UploadContentPackCommand;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PixBot extends ListenerAdapter {

	private static @Getter JDA jda;

	private MultiCommand commands = new MultiCommand("root", "");

	public static void main(String[] args) throws LoginException, FileNotFoundException {

		File file = new File("err.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setErr(ps);

		PixBot bot = new PixBot();

		bot.commands.add(new HelpCommand("My commands: ", bot.commands.commands(), ""));

		MultiCommand cp = new MultiCommand("cp", "Manage my content packs.");
		cp.add(new DetailsContentPackCommand());
		cp.add(new ListContentPackCommand());
		cp.add(new UploadContentPackCommand());
		cp.add(new GiveContentPackCommand());
		cp.add(new RemoveContentPackCommand());

		bot.commands.add(cp);
		bot.commands.add(new PlayCommand());

		jda = JDABuilder.createDefault("NzQyODI1MjU5MTkwNzE0NDM5.XzLv7g.3ENctC1QLEN7BrRK2uExCHVcYLY").addEventListeners(bot).build();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}
		Message message = event.getMessage();
		String content = message.getContentRaw();
		if (!content.startsWith("!")) {
			return;
		}
		String[] split = content.split("\\s");
		split[0] = split[0].substring(1);
		String[] args = Arrays.stream(split).filter(s -> !s.equals("")).toArray(String[]::new);
		commands.execute(event, args);
	}
}
