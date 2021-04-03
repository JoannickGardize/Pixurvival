package com.pixurvival.discordbot.command.contentpack;

import java.util.stream.Collectors;

import com.pixurvival.core.contentPack.ContentPackContext;
import com.pixurvival.discordbot.Directories;
import com.pixurvival.discordbot.command.Command;
import com.pixurvival.discordbot.util.DiscordUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ListContentPackCommand extends Command {

	private ContentPackContext context = new ContentPackContext(Directories.contentPacks());

	public ListContentPackCommand() {
		super("list", "", "Lists all my installed and playable content packs.");
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		context.refreshList();
		String message = context.list().stream().map(s -> s.getIdentifier().toString()).sorted().collect(Collectors.joining("\n", "**My installed content packs:**\n", ""));
		DiscordUtils.sendMessage(event, message);
	}

}
