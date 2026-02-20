package com.anbu.chatgames.game;

public abstract class BaseGame {

    protected String question;

    public abstract void start();

    public abstract boolean checkAnswer(String message);

    public String getQuestion() {
        return question;
    }
}