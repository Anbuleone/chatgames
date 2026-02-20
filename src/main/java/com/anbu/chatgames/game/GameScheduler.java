package com.anbu.chatgames.game;

import net.minecraft.server.MinecraftServer;

public class GameScheduler {

    private final GameManager manager;

    public GameScheduler(GameManager manager) {
        this.manager = manager;
    }

    public void tick(MinecraftServer server) {
        // Optional auto game logic here
    }
}