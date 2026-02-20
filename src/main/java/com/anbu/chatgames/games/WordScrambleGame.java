package com.anbu.chatgames.games;

import com.anbu.chatgames.game.BaseGame;

import java.util.*;

public class WordScrambleGame extends BaseGame {

    private String answer;

    private static final List<String> WORDS = Arrays.asList(
            "minecraft",
            "diamond",
            "creeper",
            "fabric",
            "mutation"
    );

    @Override
    public void start() {
        Random r = new Random();
        answer = WORDS.get(r.nextInt(WORDS.size()));
        question = "Unscramble: " + scramble(answer);
    }

    private String scramble(String word) {
        List<Character> chars = new ArrayList<>();
        for (char c : word.toCharArray()) chars.add(c);
        Collections.shuffle(chars);

        StringBuilder sb = new StringBuilder();
        for (char c : chars) sb.append(c);
        return sb.toString();
    }

    @Override
    public boolean checkAnswer(String message) {
        return message.equalsIgnoreCase(answer);
    }

    // 🔥 ADD THIS
    public String getAnswer() {
        return answer;
    }
}