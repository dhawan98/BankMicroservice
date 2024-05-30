package dev.codescreen.service;

import dev.codescreen.Event.Event;
import dev.codescreen.Model.User;
import dev.codescreen.Model.UserSnapshot;
import dev.codescreen.Service.StateRebuilder;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StateRebuilderTest {

    @Test
    void rebuildStateFromEvents() {
        StateRebuilder rebuilder = new StateRebuilder();
        Event mockEvent = mock(Event.class);
        ConcurrentHashMap<String, User> userData = new ConcurrentHashMap<>();
        UserSnapshot snapshot = new UserSnapshot(userData, 0);

        ConcurrentHashMap<String, User> result = rebuilder.rebuildStateFromEvents(Arrays.asList(mockEvent), snapshot);

        assertNotNull(result);
    }

    @Test
    void rebuildStateFromEvents_NoEvents_NoChange() {
        StateRebuilder rebuilder = new StateRebuilder();
        ConcurrentHashMap<String, User> initialData = new ConcurrentHashMap<>();
        initialData.put("user1", new User("user1", "USD", "200"));
        UserSnapshot snapshot = new UserSnapshot(initialData, 0);

        ConcurrentHashMap<String, User> result = rebuilder.rebuildStateFromEvents(Arrays.asList(), snapshot);

        assertEquals("200", result.get("user1").getBalance());
    }
}
