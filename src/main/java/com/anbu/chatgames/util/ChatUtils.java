package com.anbu.chatgames.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public class ChatUtils {

    public static void broadcast(MinecraftServer server, String message) {
        server.getPlayerManager().broadcast(Text.literal(message), false);
    }
}
