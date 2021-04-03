package com.pixurvival.discordbot.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pixurvival.discordbot.util.DiscordUtils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MultiCommand extends Command {

	private Map<String, Command> commands = new HashMap<>();

	public MultiCommand(String name, String description) {
		super(name, "<sub-command>", description + "\nType \"!" + name + " help\" for more infos.");
		add(new HelpCommand("Usage of !" + name, commands.values(), name + " "));
	}

	public void add(Command command) {
		commands.put(command.getName(), command);
	}

	public Collection<Command> commands() {
		return Collections.unmodifiableCollection(commands.values());
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		if (args.length > 0) {
			Command command = commands.get(args[0]);
			if (command != null) {
				Member member = event.getMember();
				if (member.isOwner() || command.getRequiredRole() == null || DiscordUtils.hasRole(member, command.getRequiredRole())) {
					String[] nextArgs = Arrays.stream(args).skip(1).toArray(String[]::new);
					command.execute(event, nextArgs);
				} else {
					event.getChannel().sendMessage("You can't do that.");
				}
			}
		}
	}
}
