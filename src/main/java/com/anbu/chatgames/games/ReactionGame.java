package com.anbu.chatgames.games;

import com.anbu.chatgames.game.BaseGame;

public class ReactionGame extends BaseGame {

    @Override
    public void start() {
        question = "Type NOW!";
    }

    @Override
    public boolean checkAnswer(String message) {
        return message.equalsIgnoreCase("NOW");
    }
}