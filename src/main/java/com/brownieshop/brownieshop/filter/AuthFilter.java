package com.brownieshop.brownieshop.filter;

import com.brownieshop.brownieshop.model.Customer;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI();

        // Allow these pages without login
        boolean isPublicPage = path.equals("/login") ||
                path.equals("/register") ||
                path.equals("/reset-password") ||
                path.equals("/reset-password/confirm") ||
                path.startsWith("/static/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/");

        // Check if user is logged in
        boolean isLoggedIn = session != null &&
                session.getAttribute("loggedInCustomer") != null;

        if (isPublicPage) {
            // Allow access without login
            chain.doFilter(request, response);

        } else if (!isLoggedIn) {
            // Not logged in redirect to login page
            httpResponse.sendRedirect("/login");

        } else {
            // Check admin pages
            String role = (String) session.getAttribute("role");
            if (path.startsWith("/admin/") && !"ADMIN".equals(role)) {
                // Not admin redirect to profile
                httpResponse.sendRedirect("/profile");
            } else {
                // All good continue
                chain.doFilter(request, response);
            }
        }
    }
}