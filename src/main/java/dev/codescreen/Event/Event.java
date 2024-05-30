package dev.codescreen.Event;

import dev.codescreen.Model.Amount;
import dev.codescreen.Model.User;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Event {
    private String userId;
    private String messageId;
    private Amount amount;

    public Event(String userId, String messageId, Amount amount) {
        this.userId = userId;
        this.messageId = messageId;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public Amount getAmount() {
        return amount;
    }

    public abstract void apply(ConcurrentHashMap<String, User> userData);
}
