package dev.codescreen.Service;

import dev.codescreen.Event.Event;
import dev.codescreen.Model.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service//rebuilds the events from previous snapshot
public class StateRebuilder {
    
    public ConcurrentHashMap<String, User> rebuildStateFromEvents(List<Event> events, UserSnapshot snapshot) {
        ConcurrentHashMap<String, User> rebuiltUserData = snapshot != null ? new ConcurrentHashMap<>(snapshot.getUserData()) : new ConcurrentHashMap<>();
        for (int i = snapshot != null ? snapshot.getEventIndex() + 1 : 0; i < events.size(); i++) {
            events.get(i).apply(rebuiltUserData);
        }
        return rebuiltUserData;
    }
}