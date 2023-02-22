package com.pixurvival.discordbot.command;

import lombok.Getter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Getter
public abstract class Command {

    private String name;
    private String args;
    private String description;

    public Command(String name, String args, String description) {
        super();
        this.name = name;
        this.args = args;
        this.description = getRequiredRole() != null ? description + " Requires the role " + getRequiredRole() + "." : description;
    }

    public abstract void execute(MessageReceivedEvent event, String[] args);

    public String getRequiredRole() {
        return null;
    }

}
