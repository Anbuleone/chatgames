package com.anbu.chatgames.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("config/chatgames.json");

    public int cooldownSeconds = 30;
    public int gameTimeoutSeconds = 20;
    public int rewardXP = 5;
    public int countdownSeconds = 5;

    // 🎁 Reward Settings
    public boolean enableItemRewards = true;
    public int rareChancePercent = 15;

    public List<String> commonRewards = Arrays.asList(
            "minecraft:iron_ingot",
            "minecraft:gold_ingot",
            "minecraft:diamond"
    );

    public List<String> rareRewards = Arrays.asList(
            "minecraft:nether_star",
            "minecraft:enchanted_golden_apple"
    );

    public static ModConfig INSTANCE;

    public static void load() {
        try {
            if (!FILE.exists()) {
                INSTANCE = new ModConfig();
                save();
                return;
            }

            FileReader reader = new FileReader(FILE);
            INSTANCE = GSON.fromJson(reader, ModConfig.class);
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            INSTANCE = new ModConfig();
        }
    }

    public static void save() {
        try {
            FILE.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(FILE);
            GSON.toJson(INSTANCE, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}