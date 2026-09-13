package com.expensemanager.security;

import java.io.IOException;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MemberScopeFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!"MEMBER".equals(userPrincipal.getRole())) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID memberChitId = userPrincipal.getMemberChitId();
        if (memberChitId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Member is not assigned to any chit.");
            return;
        }

        String path = request.getRequestURI();
        String chitIdParam = request.getParameter("chitId");
        String memberIdParam = request.getParameter("memberId");

        if (path.startsWith("/api/chits") || path.startsWith("/api/members")
                || path.startsWith("/api/installments") || path.startsWith("/api/auctions")
                || path.startsWith("/api/incomes")) {
            if (path.startsWith("/api/chits/") && path.endsWith("/summary")) {
                String requestedId = path.substring("/api/chits/".length(), path.length() - "/summary".length());
                if (!memberChitId.toString().equals(requestedId)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                    return;
                }
            }

            if (chitIdParam != null && !memberChitId.toString().equals(chitIdParam)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                return;
            }

            if (memberIdParam != null && !userPrincipal.getMemberId().toString().equals(memberIdParam)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
