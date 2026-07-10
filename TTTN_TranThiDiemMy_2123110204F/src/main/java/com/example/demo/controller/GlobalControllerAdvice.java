package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @ModelAttribute("unreadNotificationCount")
    public long addUnreadNotificationCount(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
            if (userOpt.isPresent()) {
                return notificationService.getUnreadCount(userOpt.get().getId());
            }
        }
        return 0;
    }
}
