package com.SWP391.G3PCoffee.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Component;

// Add this to a filter or interceptor that runs on all requests
@Component
public class SessionRestoreFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();

        // Check if session already has a sessionId
        if (session.getAttribute("sessionId") == null) {
            // Try to get from cookie
            Cookie[] cookies = httpRequest.getCookies();
            String storedSessionId = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("g3p_session_id".equals(cookie.getName())) {
                        storedSessionId = cookie.getValue();
                        break;
                    }
                }
            }

            if (storedSessionId != null) {
                // Restore the session ID
                session.setAttribute("sessionId", storedSessionId);
            } else {
                // Create a new one
                String newSessionId = UUID.randomUUID().toString();
                session.setAttribute("sessionId", newSessionId);

                // Store in cookie for future visits
                Cookie sessionCookie = new Cookie("g3p_session_id", newSessionId);
                sessionCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
                sessionCookie.setPath("/");
                httpResponse.addCookie(sessionCookie);
            }
        }

        chain.doFilter(request, response);
    }
}