package com.anbu.chatgames;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Random;

public class GameManager {

    private static MinecraftServer server;
    private static final Random RANDOM = new Random();

    private static boolean running = false;
    private static String answer = "";
    private static long startTime;

    private static int tickCounter = 0;

    // ⏱️ timings
    private static final int GAME_INTERVAL_TICKS = 20 * 60 * 5; // 5 mins
    private static final int ANSWER_TIME_SECONDS = 20;

    // ================= INIT =================
    public static void init(MinecraftServer srv) {
        server = srv;

        // ✅ CHAT LISTENER
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, player, params) -> {

            if (!running) return true;

            String msg = message.getContent().getString().trim();

            if (msg.equalsIgnoreCase(answer)) {

                long time =
                        (System.currentTimeMillis() - startTime) / 1000;

                // 🔥 IMPORTANT: next tick
                server.execute(() -> win(player, time));

                return false; // cancel chat
            }

            return true;
        });

        // SERVER TICK
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
                broadcast("§c⏰ Time's up! Correct answer: §f" + answer);
                running = false;
            }
        }
    }

    // ================= START GAME =================
    private static void startGame() {
        running = true;

        Question q = generateQuestion();
        answer = q.answer.toLowerCase();
        startTime = System.currentTimeMillis();

        broadcast("§6✦ CHAT GAMES ✦");
        broadcast("§e" + q.question);
        broadcast("§7You have §a" + ANSWER_TIME_SECONDS + "§7 seconds!");
    }

    // ================= WIN =================
    private static void win(ServerPlayerEntity player, long time) {
        if (!running) return;
        running = false;

        giveRandomReward(player);

        broadcast("§a✔ " + player.getName().getString()
                + " answered correctly in §e" + time + "§a seconds!");
    }

    // ================= QUESTIONS =================
    private static Question generateQuestion() {

        int type = RANDOM.nextInt(4);

        // 🔢 MATH
        if (type == 0) {
            int a = RANDOM.nextInt(20) + 1;
            int b = RANDOM.nextInt(20) + 1;
            return new Question(
                    "Solve: " + a + " + " + b,
                    String.valueOf(a + b)
            );
        }

        // 🔤 REVERSE
        if (type == 1) {
            String[] words = {"JAVA", "MINECRAFT", "DIAMOND", "SERVER"};
            String w = words[RANDOM.nextInt(words.length)];
            return new Question(
                    "Reverse the word: " + w,
                    new StringBuilder(w).reverse().toString().toLowerCase()
            );
        }

        // 🧱 MINECRAFT
        if (type == 2) {
            Question[] mc = {
                    new Question("Which mob drops Ender Pearl?", "enderman"),
                    new Question("Which block is used for beacons?", "iron"),
                    new Question("Which dimension has Netherite?", "nether")
            };
            return mc[RANDOM.nextInt(mc.length)];
        }

        // 🎮 FUN
        Question[] fun = {
                new Question("Type GG", "gg"),
                new Question("Type EZ", "ez"),
                new Question("Type LOL", "lol")
        };
        return fun[RANDOM.nextInt(fun.length)];
    }

    // ================= RANDOM REWARD =================
    private static void giveRandomReward(ServerPlayerEntity p) {

        ItemStack[] rewards = {
                new ItemStack(Items.IRON_BLOCK, 5),
                new ItemStack(Items.GOLD_BLOCK, 3),
                new ItemStack(Items.DIAMOND, 3),
                new ItemStack(Items.EMERALD, 4)
        };

        ItemStack reward =
                rewards[RANDOM.nextInt(rewards.length)].copy();

        // 🔒 SAVE BEFORE INSERT
        int count = reward.getCount();
        String itemName = reward.getName().getString();

        // 🎒 GIVE ITEM
        p.getInventory().insertStack(reward);

        // 💬 CHAT MESSAGE
        p.sendMessage(
                Text.literal("§6✦ CHAT GAMES ✦")
                        .append("\n§a✔ Reward Received!")
                        .append("\n§e➜ " + count + " x " + itemName),
                false
        );
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
