package com.grambasket.authservice.security;

import com.grambasket.authservice.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.trace("No JWT token found for request to '{}'. Proceeding with filter chain.", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(BEARER_PREFIX.length());
            final String userId = jwtService.extractUsername(jwt);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("JWT token found for user ID: {}. Attempting to authenticate.", userId);

                UserDetails userDetails = this.userRepository.findById(userId)
                        .orElse(null);

                if (userDetails == null) {
                    log.warn("User with ID '{}' from token not found in the database.", userId);
                } else if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.info("Token is valid for user ID: {}. Setting security context.", userId);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // The JwtService already logs the specific reason for invalidity (e.g., expired)
                    log.warn("Token validation failed for user ID: {}.", userId);
                }
            }
        } catch (Exception e) {
            // Specific JWT exceptions (Expired, Malformed, etc.) are already logged within JwtService.
            // This block catches any other unexpected errors during the authentication process.
            log.error("An unexpected error occurred during JWT authentication for request to '{}'", request.getRequestURI(), e);
        }

        filterChain.doFilter(request, response);
    }
}