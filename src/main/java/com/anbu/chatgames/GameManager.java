package com.anbu.chatgames;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Random;

public class GameManager {

    private static MinecraftServer server;
    private static final Random RANDOM = new Random();

    private static boolean running = false;
    private static String answer = "";
    private static long startTime;

    private static int tickCounter = 0;

    private static final int GAME_INTERVAL_TICKS = 20 * 60; // 60 sec
    private static final int ANSWER_TIME_SECONDS = 20;

    // ================= INIT =================
    public static void init(MinecraftServer srv) {
        server = srv;

        ServerMessageEvents.CHAT_MESSAGE.register((message, player, params) -> {
            if (!running) return;

            String msg = message.getContent().getString().trim();
            if (msg.equalsIgnoreCase(answer)) {
                long time =
                        (System.currentTimeMillis() - startTime) / 1000;
                win(player, time);
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(s -> tick());
    }

    // ================= TICK =================
    private static void tick() {
        tickCounter++;

        if (!running && tickCounter >= GAME_INTERVAL_TICKS) {
            tickCounter = 0;
            startGame();
        }

        if (running) {
            long elapsed =
                    (System.currentTimeMillis() - startTime) / 1000;
            if (elapsed >= ANSWER_TIME_SECONDS) {
                broadcast("§cTime's up! Correct answer: §f" + answer);
                running = false;
            }
        }
    }

    // ================= START GAME =================
    private static void startGame() {
        running = true;

        Question q = generateQuestion();
        answer = q.answer;
        startTime = System.currentTimeMillis();

        broadcast("§6✦ CHAT GAMES ✦");
        broadcast("§e" + q.question);
        broadcast("§7You have §a" + ANSWER_TIME_SECONDS + "§7 seconds");

        playStartSound();
    }

    // ================= WIN =================
    private static void win(ServerPlayerEntity player, long time) {
        if (!running) return;
        running = false;

        giveReward(player);
        playWinEffects(player);

        broadcast("§a✔ " + player.getName().getString()
                + " answered in §e" + time + "§a seconds!");
    }

    // ================= QUESTIONS =================
    private static Question generateQuestion() {
        int t = RANDOM.nextInt(3);

        if (t == 0) {
            int a = RANDOM.nextInt(20) + 1;
            int b = RANDOM.nextInt(20) + 1;
            return new Question("Solve: " + a + " + " + b,
                    String.valueOf(a + b));
        }

        if (t == 1) {
            String w = "Water";
            return new Question("Reverse: " + w,
                    new StringBuilder(w).reverse().toString());
        }

        return new Question(
                "Which block is used for beacons?",
                "iron"
        );
    }

    // ================= REWARD =================
    private static void giveReward(ServerPlayerEntity p) {
        p.getInventory().insertStack(
                new ItemStack(Items.IRON_BLOCK, 5)
        );
    }

    // ================= SOUNDS =================
    private static void playStartSound() {
        SoundEvent sound = Registries.SOUND_EVENT.get(
                Identifier.of("minecraft", "block.note_block.pling")
        );

        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            p.getWorld().playSound(
                    null,
                    p.getBlockPos(),
                    sound,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.2f
            );
        }
    }

    private static void playWinEffects(ServerPlayerEntity player) {
        SoundEvent sound = Registries.SOUND_EVENT.get(
                Identifier.of("minecraft", "ui.toast.challenge_complete")
        );

        World w = player.getWorld();
        w.playSound(
                null,
                player.getBlockPos(),
                sound,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
        );

        spawnFirework(player);
    }

    // ================= FIREWORK =================
    private static void spawnFirework(ServerPlayerEntity player) {
        World w = player.getWorld();
        FireworkRocketEntity fw = new FireworkRocketEntity(
                w,
                player.getX(),
                player.getY(),
                player.getZ(),
                ItemStack.EMPTY
        );
        w.spawnEntity(fw);
    }

    // ================= CHAT =================
    private static void broadcast(String msg) {
        server.getPlayerManager().broadcast(
                Text.literal(msg).formatted(Formatting.GOLD),
                false
        );
    }

    // ================= RECORD =================
    private record Question(String question, String answer) {}
}
