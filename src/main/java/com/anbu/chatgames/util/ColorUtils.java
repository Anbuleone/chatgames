package com.anbu.chatgames.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ColorUtils {

    private static final Formatting[] COLORS = {
            Formatting.RED,
            Formatting.GOLD,
            Formatting.YELLOW,
            Formatting.GREEN,
            Formatting.AQUA,
            Formatting.BLUE,
            Formatting.LIGHT_PURPLE
    };

    public static MutableText rainbow(String text) {
        MutableText result = Text.literal("");
        int i = 0;
        for (char c : text.toCharArray()) {
            result.append(Text.literal(String.valueOf(c))
                    .formatted(COLORS[i % COLORS.length]));
            i++;
        }
        return result;
    }
}