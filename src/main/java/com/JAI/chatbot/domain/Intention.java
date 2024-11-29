package com.JAI.chatbot.domain;

public enum Intention {
    EVENT("EVENT"),
    TODO("TODO"),
    PLAN_RECOMMENDATION("PLAN_RECOMMENDATION"),
    EXCEPTION("EXCEPTION");

    private final String value;
    Intention(String value) {
        this.value = value;
    }
}
