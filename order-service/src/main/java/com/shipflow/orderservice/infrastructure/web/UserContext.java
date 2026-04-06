package com.shipflow.orderservice.infrastructure.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Gateway에서 전달된 X-User-Id / X-User-Role 헤더를 파싱하는 컴포넌트.
 * JWT 방식으로 전환 시 이 클래스만 수정한다.
 */
@Component
public class UserContext {

    public UUID getUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("X-User-Id 헤더가 없습니다.");
        }
        return UUID.fromString(userId);
    }

    public String getUserRole(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("X-User-Role 헤더가 없습니다.");
        }
        return role;
    }
}
