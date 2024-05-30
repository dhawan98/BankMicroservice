package dev.codescreen.Model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Ping {
    private final String serverTime;

    public Ping(ZonedDateTime dateTime) {
        this.serverTime = dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    // Getter
    public String getServerTime() {
        return serverTime;
    }
}
