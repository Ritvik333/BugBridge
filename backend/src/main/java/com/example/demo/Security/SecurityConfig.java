package com.example.demo.Security;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final UserAuthenticationProvider userAuthenticationProvider;

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .cors(cors -> cors.configurationSource(request -> {
//                    CorsConfiguration config = new CorsConfiguration();
//                    config.setAllowedOrigins(List.of("http://localhost:3000","http://172.17.3.8:3000/","http://172.17.3.8:3030/","http://172.17.3.8:3080/")); // Allow frontend origin
//                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//                    config.setAllowedHeaders(List.of("*"));
//                    config.setAllowCredentials(true);
//                    return config;
//                }))
//                .csrf(csrf -> csrf.disable())
//                .securityMatcher("/**")
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/**").permitAll()
//                        .requestMatchers("/api/**").permitAll()
//                        .requestMatchers("/drafts/**").permitAll()
//                        .requestMatchers("/submissions/**").permitAll()
//                        .requestMatchers("/notifications/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/protected-endpoint").authenticated()
//                        .requestMatchers(HttpMethod.GET, "/api/suggestions").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .exceptionHandling(ex -> ex.authenticationEntryPoint(userAuthenticationEntryPoint))
//                .addFilterBefore(new JwtAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
//                .build();
//    }
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .cors(cors -> cors.configurationSource(this::corsConfiguration))
            .csrf(csrf -> csrf.disable())
            .securityMatcher("/**")
            .authorizeHttpRequests(auth -> configureAuthorization(auth))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(userAuthenticationEntryPoint))
            .addFilterBefore(new JwtAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
            .build();
}

    private CorsConfiguration corsConfiguration(HttpServletRequest request) {
        List<String> allowedOrigins = List.of(
                "http://localhost:3000",
                "http://172.17.3.8:3000/",
                "http://172.17.3.8:3030/",
                "http://172.17.3.8:3080/"
        );

        List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
        List<String> allowedHeaders = List.of("*");

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(allowedMethods);
        config.setAllowedHeaders(allowedHeaders);
        config.setAllowCredentials(true);

        return config;
    }

    private void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers("/auth/**").permitAll();
        auth.requestMatchers("/api/**").permitAll();
        auth.requestMatchers("/drafts/**").permitAll();
        auth.requestMatchers("/submissions/**").permitAll();
        auth.requestMatchers("/notifications/**").permitAll();
        auth.requestMatchers(HttpMethod.GET, "/api/protected-endpoint").authenticated();
        auth.requestMatchers(HttpMethod.GET, "/api/suggestions").permitAll();
        auth.anyRequest().authenticated();
    }


}