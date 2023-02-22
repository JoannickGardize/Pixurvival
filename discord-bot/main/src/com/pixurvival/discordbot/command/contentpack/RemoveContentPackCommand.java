package com.pixurvival.discordbot.command.contentpack;

import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.summary.ContentPackSummary;
import com.pixurvival.discordbot.util.DiscordUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveContentPackCommand extends ContentPackCommand {

    public RemoveContentPackCommand() {
        super("Remove", "Remove the given content pack from my bag.");
    }

    @Override
    protected void processContentPack(MessageReceivedEvent event, ContentPackSummary summary) {
        try {
            getContext().fileOf(summary.getIdentifier()).delete();
        } catch (ContentPackException | SecurityException e) {
            e.printStackTrace();
            DiscordUtils.sendMessage(event, "Oh, Something went wrong with the file.");
        }
    }

    @Override
    public String getRequiredRole() {
        return DiscordUtils.CONTENT_PACK_MANAGER_ROLE;
    }
}
