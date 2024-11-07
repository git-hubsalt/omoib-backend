package com.githubsalt.omoib.notification;

import com.githubsalt.omoib.global.config.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtProvider jwtProvider;

    @GetMapping("")
    public List<Notification> getNotifications(HttpServletRequest request) {
        Long userId = jwtProvider.getUserId(request);
        return notificationService.getAllUnNotifiedNotifications(userId);
    }

}
