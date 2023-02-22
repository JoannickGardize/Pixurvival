package com.pixurvival.gdxcore.benchmark;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        DiscordRPC.discordInitialize("742825259190714439", new DiscordEventHandlers.Builder().setReadyEventHandler(user -> System.out.println(user + " is Ready !")).build(), true);

        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            DiscordRPC.discordRunCallbacks();
            DiscordRichPresence rich = new DiscordRichPresence.Builder("Testing").setParty("Party test", 1, 10).setDetails("This is the details").setBigImage("pixurvival_cover", "Pixurvival cover")
                    .build();
            DiscordRPC.discordUpdatePresence(rich);
        }

        DiscordRPC.discordShutdown();
    }
}
