package com.forcreators.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int LIMIT = 8;
    private static final long WINDOW_MS = 60_000;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        boolean limited = "POST".equalsIgnoreCase(request.getMethod())
                && (path.equals("/api/contact") || path.equals("/api/custom-orders") || path.equals("/api/newsletter"));
        if (limited) {
            String key = request.getRemoteAddr() + ":" + path;
            Window window = windows.compute(key, (k, existing) -> {
                long now = Instant.now().toEpochMilli();
                if (existing == null || now - existing.start > WINDOW_MS) {
                    return new Window(now, 1);
                }
                existing.count++;
                return existing;
            });
            if (window.count > LIMIT) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Too many requests. Please try again shortly.\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private static class Window {
        final long start;
        int count;

        Window(long start, int count) {
            this.start = start;
            this.count = count;
        }
    }
}
