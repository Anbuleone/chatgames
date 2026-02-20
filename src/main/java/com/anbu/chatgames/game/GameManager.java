package com.anbu.chatgames.game;

import com.anbu.chatgames.config.ModConfig;
import com.anbu.chatgames.games.FastMathGame;
import com.anbu.chatgames.games.ReactionGame;
import com.anbu.chatgames.games.WordScrambleGame;
import com.anbu.chatgames.reward.RewardManager;
import com.anbu.chatgames.util.ColorUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.Random;
import java.util.UUID;

public class GameManager {

    private BaseGame currentGame;
    private boolean running = false;
    private long lastGameTime = 0;

    private DifficultyLevel difficulty = DifficultyLevel.EASY;

    private UUID lastWinner = null;
    private int winStreak = 0;

    public GameManager() {}

    private int cooldownSeconds() {
        return ModConfig.INSTANCE.cooldownSeconds;
    }

    private int gameTimeoutSeconds() {
        return ModConfig.INSTANCE.gameTimeoutSeconds;
    }

    private int baseRewardXP() {
        return ModConfig.INSTANCE.rewardXP;
    }

    private Text prefix() {
        return Text.literal("§7✦ ")
                .append(ColorUtils.rainbow("CHAT GAMES"))
                .append(Text.literal(" §7✦ "));
    }

    public void setDifficulty(DifficultyLevel level) {
        this.difficulty = level;
    }

    public void startRandomGame(MinecraftServer server) {

        long now = System.currentTimeMillis();

        if (running) {
            server.getPlayerManager().broadcast(
                    prefix().copy().append(Text.literal("§c A game is already running!")),
                    false
            );
            return;
        }

        if ((now - lastGameTime) < cooldownSeconds() * 1000L) {
            server.getPlayerManager().broadcast(
                    prefix().copy().append(Text.literal("§c Game is on cooldown!")),
                    false
            );
            return;
        }

        startGame(server);
    }

    public void stopGame(MinecraftServer server) {

        if (!running) {
            server.getPlayerManager().broadcast(
                    prefix().copy().append(Text.literal("§c No game is running.")),
                    false
            );
            return;
        }

        running = false;
        currentGame = null;

        server.getPlayerManager().broadcast(
                prefix().copy().append(Text.literal("§e Game stopped.")),
                false
        );
    }

    private void startGame(MinecraftServer server) {

        Random r = new Random();
        int pick = r.nextInt(3);

        if (pick == 0) currentGame = new FastMathGame(difficulty);
        else if (pick == 1) currentGame = new WordScrambleGame();
        else currentGame = new ReactionGame();

        currentGame.start();
        running = true;
        lastGameTime = System.currentTimeMillis();

        server.getPlayerManager().broadcast(
                prefix().copy().append(
                        Text.literal("§f Difficulty: §e" + difficulty.name()
                                + " §f| You have "
                                + gameTimeoutSeconds()
                                + " seconds to "
                                + currentGame.getQuestion())
                ),
                false
        );

        startTimeout(server);
    }

    private void startTimeout(MinecraftServer server) {

        int timeout = gameTimeoutSeconds();

        new Thread(() -> {
            try {
                Thread.sleep(timeout * 1000L);

                if (running) {

                    String reveal = "Unknown";

                    if (currentGame instanceof FastMathGame math) {
                        reveal = String.valueOf(math.getAnswer());
                    } else if (currentGame instanceof WordScrambleGame scramble) {
                        reveal = scramble.getAnswer();
                    } else if (currentGame instanceof ReactionGame) {
                        reveal = "NOW";
                    }

                    running = false;
                    currentGame = null;

                    winStreak = 0;
                    lastWinner = null;

                    String finalReveal = reveal;

                    server.execute(() ->
                            server.getPlayerManager().broadcast(
                                    prefix().copy().append(
                                            Text.literal("§c Time's up! Correct answer was: §e" + finalReveal)
                                    ),
                                    false
                            )
                    );
                }

            } catch (InterruptedException ignored) {}
        }).start();
    }

    public void handleChat(String message, ServerPlayerEntity player) {

        if (!running || currentGame == null) return;

        if (currentGame.checkAnswer(message)) {

            running = false;
            currentGame = null;

            if (lastWinner != null && lastWinner.equals(player.getUuid())) {
                winStreak++;
            } else {
                winStreak = 1;
                lastWinner = player.getUuid();
            }

            int multiplier = Math.min(winStreak, 3);
            int finalXP = baseRewardXP() * multiplier;

            player.addExperience(finalXP);

            RewardManager.giveReward(player);

            player.playSound(
                    SoundEvents.ENTITY_PLAYER_LEVELUP,
                    1.0f,
                    1.0f
            );

            String streakText = winStreak > 1
                    ? " §6(" + winStreak + " Win Streak!)"
                    : "";

            player.getServer().getPlayerManager().broadcast(
                    prefix().copy().append(
                            Text.literal("§a "
                                            + player.getName().getString()
                                            + " won! +" + finalXP + " XP")
                                    .append(Text.literal(streakText))
                    ),
                    false
            );
        }
    }

    public boolean isRunning() {
        return running;
    }
}