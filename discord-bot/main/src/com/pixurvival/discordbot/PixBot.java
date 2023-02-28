package com.pixurvival.discordbot;

import com.pixurvival.discordbot.command.HelpCommand;
import com.pixurvival.discordbot.command.MultiCommand;
import com.pixurvival.discordbot.command.PlayCommand;
import com.pixurvival.discordbot.command.contentpack.*;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class PixBot extends ListenerAdapter {

    private static @Getter JDA jda;

    private static String token = "copy/paste your token here";

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

        jda = JDABuilder.createDefault(token).addEventListeners(bot).build();
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
