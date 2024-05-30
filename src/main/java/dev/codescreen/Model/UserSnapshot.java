package dev.codescreen.Model;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class UserSnapshot implements Serializable {
    private final ConcurrentHashMap<String, User> userData;
    private final int eventIndex;

    public UserSnapshot(ConcurrentHashMap<String, User> userData, int eventIndex) {
        this.userData = userData;
        this.eventIndex = eventIndex;
    }

    public ConcurrentHashMap<String, User> getUserData() {
        return userData;
    }

    public int getEventIndex() {
        return eventIndex;
    }
}
