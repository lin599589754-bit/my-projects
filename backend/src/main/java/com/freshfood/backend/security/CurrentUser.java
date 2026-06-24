package com.freshfood.backend.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("请先登录");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Jwt jwt)) {
            throw new AccessDeniedException("登录状态无效");
        }

        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException e) {
            throw new AccessDeniedException("登录用户无效");
        }
    }

    public void requireSameUser(Long userId) {
        Long currentUserId = getUserId();

        if (!currentUserId.equals(userId)) {
            throw new AccessDeniedException("不能访问其他用户的数据");
        }
    }
}
