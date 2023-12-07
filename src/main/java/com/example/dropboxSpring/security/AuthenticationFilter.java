package com.example.dropboxSpring.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 *
 */
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
    private UserDetailsService userDetailsService;

    private JwtUtility jwtUtility;

    public AuthenticationFilter(
            UserDetailsService userDetailsService,
            JwtUtility jwtUtility
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtUtility = jwtUtility;
    }

    /**
     * Method for filtering the authorization of the security
     * Fetches the token from authorization header and does
     * codes to check if the token is valid and if the user exists / is valid.
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        //Authorization header
        var authHeader = request.getHeader("Authorization");
        // Bearer
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        jwtUtility.validateToken(token);
        String email = jwtUtility.extractEmail(token);


        if (email != null && jwtUtility.validateToken(token)) {

            var user = this.userDetailsService.loadUserByUsername(email);
            if (user != null) {

                var auth = new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword(),
                        user.getAuthorities());

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("Email of token holder : {}", email);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);

    }
}
