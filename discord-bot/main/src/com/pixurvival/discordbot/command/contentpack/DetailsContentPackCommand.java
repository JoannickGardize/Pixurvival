package com.pixurvival.discordbot.command.contentpack;

import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.IntegerInterval;
import com.pixurvival.core.contentPack.summary.ContentPackSummary;
import com.pixurvival.core.contentPack.summary.GameModeSummary;
import com.pixurvival.core.util.LocaleUtils;
import com.pixurvival.core.util.ReleaseVersion;
import com.pixurvival.discordbot.util.DiscordUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DetailsContentPackCommand extends ContentPackCommand {

	public DetailsContentPackCommand() {
		super("details", "Prints details of the given content pack.");
	}

	@Override
	protected void processContentPack(MessageReceivedEvent event, ContentPackSummary summary) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(summary.getIdentifier().toString());
		try {
			builder.setFooter("Game version: " + ReleaseVersion.displayNameOf(summary.getReleaseVersion()) + ", File size: "
					+ getContext().fileOf(summary.getIdentifier()).length() / 1024 + " KB");
		} catch (ContentPackException e) {
			builder.setFooter("Game version: " + ReleaseVersion.displayNameOf(summary.getReleaseVersion()) + ", File size: file not found!");
		}
		builder.setDescription("Game modes:");
		String languageTag = null;
		for (GameModeSummary gms : summary.getGameModeSummaries()) {
			if (languageTag == null) {
				languageTag = LocaleUtils.findBestMatch(DiscordUtils.getLocalePriority(), LocaleUtils.toLocale(gms.getNameTranslations().keySet())).toLanguageTag();
			}
			IntegerInterval teamNum = gms.getTeamNumberInterval();
			IntegerInterval teamSize = gms.getTeamSizeInterval();
			builder.addField(gms.getNameTranslations().get(languageTag),
					buildIntervalPrint("Number of team: ", teamNum) + "\n" + buildIntervalPrint("Team size: ", teamSize) + "\n" + gms.getDescriptionTranslations().get(languageTag),
					false);
		}
		DiscordUtils.sendEmbed(event, builder);
	}

	private String buildIntervalPrint(String preffix, IntegerInterval interval) {
		if (interval.getMin() == interval.getMax()) {
			return preffix + interval.getMin();
		} else {
			return preffix + interval.getMin() + " - " + interval.getMax();
		}
	}

}
