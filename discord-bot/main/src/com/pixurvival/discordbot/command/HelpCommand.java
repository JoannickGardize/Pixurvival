package com.pixurvival.discordbot.command;

import java.util.Collection;

import com.pixurvival.discordbot.util.DiscordUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand extends Command {

	private String commandPreffix;
	private String descriptionMessage;
	private Collection<Command> commands;

	public HelpCommand(String descriptionMessage, Collection<Command> commands, String commandPreffix) {
		super("help", "", "Display available commands.");
		this.descriptionMessage = descriptionMessage;
		this.commands = commands;
		this.commandPreffix = commandPreffix;
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setDescription("<:pix_robot:804029124875255838> " + descriptionMessage);
		builder.setFooter("<> = mandatory parameter, () = optional parameter");
		commands.forEach(c -> builder.addField("!" + commandPreffix + c.getName() + " " + c.getArgs(), c.getDescription(), false));
		DiscordUtils.sendEmbed(event, builder);
	}
}
