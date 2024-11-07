package com.githubsalt.omoib.notification;

import com.githubsalt.omoib.history.History;
import com.githubsalt.omoib.history.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final HistoryService historyService;

    @Transactional
    protected List<Notification> createNotifications(Long userId) {
        List<History> unNotifiedHistory = historyService.findUnNotifiedHistory(userId);
        if (unNotifiedHistory.isEmpty()) {
            return null;
        }
        List<Notification> notifications = new ArrayList<>();
        for (History history : unNotifiedHistory) {
            notifications.add(Notification.of(history));
            history.setNotifyStatus(NotifyStatus.NOTIFIED);
            historyService.updateHistory(history);
        }
        return notifications;
    }

    @Transactional
    public List<Notification> getAllUnNotifiedNotifications(Long userId) {
        return createNotifications(userId);
    }

}
