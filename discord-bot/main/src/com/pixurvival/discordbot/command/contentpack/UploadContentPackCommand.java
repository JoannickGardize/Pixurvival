package com.pixurvival.discordbot.command.contentpack;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.serialization.ContentPackSerialization;
import com.pixurvival.core.contentPack.validation.ContentPackValidator;
import com.pixurvival.core.contentPack.validation.ErrorNode;
import com.pixurvival.core.contentPack.validation.ErrorToString;
import com.pixurvival.core.util.ReleaseVersion;
import com.pixurvival.discordbot.Directories;
import com.pixurvival.discordbot.command.Command;
import com.pixurvival.discordbot.util.DiscordUtils;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UploadContentPackCommand extends Command {

	public static final int MAX_SIZE = 20 * 1024 * 1024;

	private ContentPackValidator validator = new ContentPackValidator();
	private ContentPackSerialization serialization = new ContentPackSerialization();

	public UploadContentPackCommand() {
		super("upload", "", "Uploads the attached content pack file to me, so it can be played in my server. The file must be a valid content pack and should not exceed 20 MB.");
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		Attachment attachment = DiscordUtils.getAttachmentIfAny(event.getMessage());
		if (attachment == null) {
			DiscordUtils.sendMessage(event, "You forgot to attach your content pack file.");
			return;
		}
		if (attachment.getSize() > MAX_SIZE) {
			DiscordUtils.sendMessage(event, "Oh darling! This is too big for me. (Max 20 MB)");
			return;
		}
		File file;
		try {
			file = attachment.downloadToFile(new File(Directories.tmp(), attachment.getFileName())).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			DiscordUtils.sendMessage(event, "Something gone wrong when I tried to download the file.");
			return;
		}
		ContentPack contentPack;
		try {
			contentPack = serialization.load(file);
		} catch (ContentPackException e) {
			e.printStackTrace();
			DiscordUtils.sendMessage(event, "Something seems wrong with your content pack file. Note that my version is " + ReleaseVersion.actual().displayName());
			file.delete();
			return;
		}
		List<ErrorNode> errors = validator.validate(contentPack).asList();
		if (!errors.isEmpty()) {
			StringBuilder sb = new StringBuilder("I reject your content pack for the following reasons:```");
			errors.stream().forEach(e -> sb.append("\n").append(e.pathString()).append(": ").append(ErrorToString.toString(e.getCause())));
			sb.append("```");
			DiscordUtils.sendMessage(event, sb.toString());
			file.delete();
			return;
		}
		File destination = new File(Directories.contentPacks(), contentPack.getIdentifier().fileName());
		if (destination.exists()) {
			if (!destination.delete()) {
				DiscordUtils.sendMessage(event, "Something unlikey happened.");
				file.delete();
				return;
			}
		}
		file.renameTo(destination);
		DiscordUtils.sendMessage(event, "I succesfully added the content pack " + contentPack.getIdentifier().fileName() + " to my list.");
	}

	@Override
	public String getRequiredRole() {
		return DiscordUtils.CONTENT_PACK_MANAGER_ROLE;
	}
}
