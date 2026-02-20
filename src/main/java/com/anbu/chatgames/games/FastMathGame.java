package com.anbu.chatgames.games;

import com.anbu.chatgames.game.BaseGame;
import com.anbu.chatgames.game.DifficultyLevel;

import java.util.Random;

public class FastMathGame extends BaseGame {

    private int answer;
    private DifficultyLevel difficulty;

    public FastMathGame(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void start() {

        Random r = new Random();

        int max = switch (difficulty) {
            case EASY -> 20;
            case MEDIUM -> 50;
            case HARD -> 100;
            case INSANE -> 200;
        };

        int a = r.nextInt(max) + 1;
        int b = r.nextInt(max) + 1;

        answer = a * b;
        question = "Solve: " + a + " x " + b;
    }

    @Override
    public boolean checkAnswer(String message) {
        try {
            return Integer.parseInt(message.trim()) == answer;
        } catch (Exception e) {
            return false;
        }
    }

    // 🔥 ADD THIS
    public int getAnswer() {
        return answer;
    }
}