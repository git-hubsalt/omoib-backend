package com.githubsalt.omoib.notification;

import com.githubsalt.omoib.history.History;
import com.githubsalt.omoib.history.HistoryType;

import java.time.format.DateTimeFormatter;

public record Notification(String timestamp, HistoryType type, Long historyId) {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static Notification of(History history) {
        return new Notification(getFormattedDate(history), history.getType(), history.getId());
    }

    private static String getFormattedDate(History history) {
        return history.getDate().format(DATE_TIME_FORMATTER);
    }
}

