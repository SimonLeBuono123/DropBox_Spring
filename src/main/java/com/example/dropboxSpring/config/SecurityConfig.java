package com.example.dropboxSpring.config;

import com.example.dropboxSpring.repositories.UserRepository;
import com.example.dropboxSpring.services.UserDetailsServiceImpl;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
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
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {

        security                            //for POST methods only
                .csrf(AbstractHttpConfigurer::disable
                )
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize

                        .anyRequest().denyAll())
                .authenticationProvider(authenticationProvider())
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                ));
        return security.build();
    }
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository){
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    )throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        var dao = new DaoAuthenticationProvider();
        dao.setUserDetailsService(userDetailsService(userRepo));
        dao.setPasswordEncoder(encoder());
        return dao;
    }

    @Bean
    public PasswordEncoder encoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
