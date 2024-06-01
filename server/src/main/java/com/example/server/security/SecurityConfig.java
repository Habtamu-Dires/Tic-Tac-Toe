package com.example.server.security;

import com.example.server.jwt.JwtAuthenticationFilter;
import com.example.server.jwt.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LogoutService logoutService;
    private final CustomUserDetailService customUserDetailService;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomPasswordEncoder passwordEncoder;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //jwt filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(cors -> cors
                .configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(List.of(frontendUrl));
                    configuration.setAllowedHeaders(List.of("Origin", "Content-Type", "Accept", "Authorization"));
                    configuration.setAllowedMethods(List.of("*"));
                    configuration.setAllowCredentials(true);
                    return configuration;
                })
        );

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST,"/api/v1/auth/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users").hasAuthority("Admin")
                .anyRequest().authenticated()
        );

        http.sessionManagement(sessison -> sessison       // b/c jwt
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.logout(logout -> logout
                .addLogoutHandler(logoutService)
                .logoutUrl("/api/v1/auth/logout")
                .logoutSuccessHandler(((request, response, authentication) ->
                        SecurityContextHolder.clearContext()))
        );

        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/oauth2/authorization")
                .authorizationEndpoint(endPoint -> endPoint
                         .baseUri("/oauth2/authorization")
                )
                .userInfoEndpoint(userInfoEndPoint -> userInfoEndPoint
                        .userService(customOAuth2UserService)
                )
                .successHandler(oAuth2LoginSuccessHandler)
        );

        return http.build();
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder.passwordEncoder());
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }



//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOriginPatterns(List.of(frontendUrl)); // Use allowedOriginPatterns instead of allowedOrigins
//        configuration.setAllowedHeaders(List.of("Origin", "Content-Type", "Accept", "Authorization"));
//        configuration.setAllowedMethods(List.of("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
}
