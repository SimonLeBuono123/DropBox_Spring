package com.example.dropboxSpring.security;

import com.example.dropboxSpring.repositories.UserRepository;
import com.example.dropboxSpring.services.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepo;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security, UserDetailsService userDetailsService, JwtUtility jwtUtility) throws Exception {

        var user = AuthorityAuthorizationManager.<RequestAuthorizationContext>hasRole("USER");
        var admin = AuthorityAuthorizationManager.<RequestAuthorizationContext >hasRole("ADMIN");
        user.setRoleHierarchy(roleHierarchy());
        admin.setRoleHierarchy(roleHierarchy());


        security
                .csrf(AbstractHttpConfigurer::disable
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login", "/user/register").permitAll()
                        .requestMatchers("/file/upload", "/folder/create").access(user)
                        .requestMatchers("/adminTest").access(admin)
                        .anyRequest().denyAll())
                .authenticationProvider(authenticationProvider())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new AuthenticationFilter(userDetailsService, jwtUtility), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                ));
        return security.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }
    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl role = new RoleHierarchyImpl();
        role.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return role;
    }
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        var dao = new DaoAuthenticationProvider();
        dao.setUserDetailsService(userDetailsService(userRepo));
        dao.setPasswordEncoder(encoder());
        return dao;
    }

    @Bean
    public PasswordEncoder encoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
