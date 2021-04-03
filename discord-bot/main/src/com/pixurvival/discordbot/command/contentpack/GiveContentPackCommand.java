package com.pixurvival.discordbot.command.contentpack;

import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.summary.ContentPackSummary;
import com.pixurvival.discordbot.util.DiscordUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GiveContentPackCommand extends ContentPackCommand {

	public GiveContentPackCommand() {
		super("give", "Provides the given content pack for download.");
	}

	@Override
	protected void processContentPack(MessageReceivedEvent event, ContentPackSummary summary) {
		try {
			event.getChannel().sendMessage("Here it is.").addFile(getContext().fileOf(summary.getIdentifier())).queue();
		} catch (ContentPackException e) {
			DiscordUtils.sendMessage(event, "Doh! Something went wrong with my internal content pack file.");
		}
	}

}
