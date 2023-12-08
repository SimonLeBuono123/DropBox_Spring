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

/**
 * Method for configuring the security of application and
 * all http routes.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepo;

    /**
     * Method for filtering and altering the routes of application
     * Such as making some routes only accessible with role User or higher (Admin).
     * And adding the token authentication
     *
     * @param security
     * @param userDetailsService
     * @param jwtUtility
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity security,
            UserDetailsService userDetailsService,
            JwtUtility jwtUtility) throws Exception {

        var user =
                AuthorityAuthorizationManager.
                        <RequestAuthorizationContext>hasRole("USER");

        var admin =
                AuthorityAuthorizationManager.
                        <RequestAuthorizationContext>hasRole("ADMIN");

        user.setRoleHierarchy(roleHierarchy());
        admin.setRoleHierarchy(roleHierarchy());


        security
                .csrf(AbstractHttpConfigurer::disable
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login", "/user/register").permitAll()
                        .requestMatchers("/file/**", "/folder/create").access(user)
                        .requestMatchers("/adminTest").access(admin)
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        new AuthenticationFilter(userDetailsService, jwtUtility)
                        , UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                ));
        return security.build();
    }

    /**
     * Method for finding and loading the user from the database.
     * @param userRepository
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }

    /**
     * Method for setting up the hierarchy of the roles
     * of the routes. So admin being higher in the hierarchy
     * means admin roles has access to User roles but
     * users do not have access to admin roles.
     *
     * @return
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl role = new RoleHierarchyImpl();
        role.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return role;
    }

    /**
     * Method for management of authentication object which here is authentication provider
     * and is also used in loginService when checking if the user
     * has entered credentials.
     * It is also part of the security filter chain and is checked
     * on all routes that needs an authentication.
     *
     * @param config
     * @return
     * @throws Exception
     */
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Method for checking credentials(email and password) of the user when logging in
     * and also when logged in. Returns unauthorized if credentials are incorrect
     * on login.
     *
     * @return
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        var dao = new DaoAuthenticationProvider();
        dao.setUserDetailsService(userDetailsService(userRepo));
        dao.setPasswordEncoder(encoder());
        return dao;
    }

    /**
     * Method for encrypting a password with bcrypt encryption
     *
     * @return
     */
    @Bean
    public PasswordEncoder encoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
