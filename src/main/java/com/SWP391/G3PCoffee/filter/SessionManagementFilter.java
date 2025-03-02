//package com.SWP391.G3PCoffee.filter;
//
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.UUID;
//
//import org.springframework.stereotype.Component;
//
//// Add this to a filter or interceptor that runs on all requests
//@Component
//public class SessionManagementFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpSession session = httpRequest.getSession();
//
//        // Check if sessionId exists, if not create one
//        if (session.getAttribute("sessionId") == null) {
//            String sessionId = UUID.randomUUID().toString();
//            session.setAttribute("sessionId", sessionId);
//        }
//
//        // Store client IP for payment processing
//        session.setAttribute("clientIpAddress", httpRequest.getRemoteAddr());
//
//        chain.doFilter(request, response);
//    }
//}