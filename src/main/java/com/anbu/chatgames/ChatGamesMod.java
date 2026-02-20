package com.anbu.chatgames;

import com.anbu.chatgames.config.ModConfig;
import com.anbu.chatgames.game.DifficultyLevel;
import com.anbu.chatgames.game.GameManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

public class ChatGamesMod implements ModInitializer {

    private static final GameManager manager = new GameManager();

    @Override
    public void onInitialize() {

        ModConfig.load();

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            if (sender instanceof ServerPlayerEntity player) {
                manager.handleChat(message.getContent().getString(), player);
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(literal("chatgame")
                    .then(literal("start")
                            .executes(ctx -> {
                                manager.startRandomGame(ctx.getSource().getServer());
                                return 1;
                            }))
                    .then(literal("stop")
                            .executes(ctx -> {
                                manager.stopGame(ctx.getSource().getServer());
                                return 1;
                            }))
                    .then(literal("difficulty")
                            .then(literal("easy").executes(ctx -> {
                                manager.setDifficulty(DifficultyLevel.EASY);
                                return 1;
                            }))
                            .then(literal("medium").executes(ctx -> {
                                manager.setDifficulty(DifficultyLevel.MEDIUM);
                                return 1;
                            }))
                            .then(literal("hard").executes(ctx -> {
                                manager.setDifficulty(DifficultyLevel.HARD);
                                return 1;
                            }))
                            .then(literal("insane").executes(ctx -> {
                                manager.setDifficulty(DifficultyLevel.INSANE);
                                return 1;
                            }))
                    )
            );
        });
    }
}