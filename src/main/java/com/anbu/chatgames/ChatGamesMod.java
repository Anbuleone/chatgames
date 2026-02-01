package com.anbu.chatgames;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ChatGamesMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(GameManager::init);
        System.out.println("[ChatGames] Loaded");
    }
}
