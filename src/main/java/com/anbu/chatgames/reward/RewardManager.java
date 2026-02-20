package com.anbu.chatgames.reward;

import com.anbu.chatgames.config.ModConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Random;

public class RewardManager {

    private static final Random RANDOM = new Random();

    public static void giveReward(ServerPlayerEntity player) {

        System.out.println("[ChatGames] Reward triggered for " + player.getName().getString());

        if (!ModConfig.INSTANCE.enableItemRewards) {
            player.sendMessage(Text.literal("§cItem rewards disabled in config."), false);
            return;
        }

        boolean rare = RANDOM.nextInt(100) < ModConfig.INSTANCE.rareChancePercent;

        List<String> pool = rare
                ? ModConfig.INSTANCE.rareRewards
                : ModConfig.INSTANCE.commonRewards;

        if (pool == null || pool.isEmpty()) {
            player.sendMessage(Text.literal("§cReward pool empty!"), false);
            return;
        }

        String itemId = pool.get(RANDOM.nextInt(pool.size()));

        try {

            String[] parts = itemId.split(":");
            if (parts.length != 2) {
                player.sendMessage(Text.literal("§cInvalid item format: " + itemId), false);
                return;
            }

            Identifier identifier = Identifier.of(parts[0], parts[1]);

            if (!Registries.ITEM.containsId(identifier)) {
                player.sendMessage(Text.literal("§cItem not found: " + itemId), false);
                return;
            }

            Item item = Registries.ITEM.get(identifier);

            int amount = rare ? 1 : RANDOM.nextInt(4) + 1;

            ItemStack stack = new ItemStack(item, amount);

            boolean inserted = player.getInventory().insertStack(stack);

            if (!inserted) {
                player.dropItem(stack, false);
            }

            player.sendMessage(
                    Text.literal("§bYou received §e"
                            + amount + "x "
                            + itemId
                            + (rare ? " §6(RARE!)" : "")),
                    false
            );

            if (rare) {
                player.getServer().getPlayerManager().broadcast(
                        Text.literal("§6✨ " + player.getName().getString()
                                + " received a RARE reward: "
                                + itemId + "!"),
                        false
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(Text.literal("§cReward error occurred."), false);
        }
    }
}