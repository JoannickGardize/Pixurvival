package com.pixurvival.discordbot.command.contentpack;

import com.pixurvival.core.contentPack.ContentPackContext;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.contentPack.summary.ContentPackSummary;
import com.pixurvival.discordbot.Directories;
import com.pixurvival.discordbot.command.Command;
import com.pixurvival.discordbot.util.DiscordUtils;
import lombok.Getter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class ContentPackCommand extends Command {

    private @Getter ContentPackContext context = new ContentPackContext(Directories.contentPacks());

    public ContentPackCommand(String name, String description) {
        super(name, "<name> (version)", description);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (args.length == 0) {
            DiscordUtils.sendMessage(event, "You forgot to specify the name of the content pack.");
            return;
        }
        final Version version;
        if (args.length > 1) {
            try {
                version = new Version(args[1]);
            } catch (IllegalArgumentException e) {
                DiscordUtils.sendMessage(event, "This version number is illegal. It should be in the format X.Y where X and Y are integer numbers.");
                return;
            }
        } else {
            version = null;
        }
        String name = args[0].toLowerCase();
        Predicate<ContentPackSummary> condition = c -> c.getIdentifier().getName().toLowerCase().equals(name);
        if (version != null) {
            condition = condition.and(c -> c.getIdentifier().getVersion().equals(version));
        }
        context.refreshList();
        Optional<ContentPackSummary> searchResult = context.list().stream().filter(condition).findAny();
        if (searchResult.isPresent()) {
            ContentPackSummary summary = searchResult.get();
            processContentPack(event, summary);
        } else {
            DiscordUtils.sendMessage(event, "I don't have such a content pack in my bag.");
        }
    }

    protected abstract void processContentPack(MessageReceivedEvent event, ContentPackSummary summary);
}
