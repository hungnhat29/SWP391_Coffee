package com.SWP391.G3PCoffee.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;


    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(List.of(authProvider));
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/admin/dashboard","orders","/customers").hasRole("ADMIN")
//                         .requestMatchers("/track-order", "/canceled-order", "/get-order-details","/my-voucher","/claim-voucher","/profile","/profile/update").hasRole("CUSTOMER")
//                        .requestMatchers("/orders/**").hasRole("STAFF")
//                        .requestMatchers("/shop", "/detail", "/home", "/api/cart", "/api/cart/**", "/cart", "/api/checkout/process", "/purchase-history").permitAll()
//                        .requestMatchers("/auth/login", "/auth/register", "/home","/", "check-login","/api/contact/send-otp","/forgot-password","/verify-otp").permitAll()
//                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/static/**").permitAll()
//                        .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/admin/orders/**", "/orders").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                                .requestMatchers("/category/**", "/admin/dashboard", "/categories", "/products/**").hasAnyRole("ADMIN", "MANAGER")
                                .requestMatchers("users/customers/**").hasRole("ADMIN")
                                .requestMatchers("/promotions/api/active").permitAll()
                                .requestMatchers("/promotions/api/apply-to-cart").permitAll()
                                .requestMatchers("/promotions").hasRole("ADMIN")
                                .requestMatchers("/promotions/create").hasRole("ADMIN")
                                .requestMatchers("/promotions/{id}").hasRole("ADMIN")
                                .requestMatchers("/promotions/{id}/edit").hasRole("ADMIN")
                                .requestMatchers("/promotions/user/{userId}").hasRole("ADMIN")
                                .requestMatchers("/promotions/api/create").hasRole("ADMIN")
                                .requestMatchers("/promotions/api/all").hasRole("ADMIN")
                                .requestMatchers("/promotions/api/{id}").hasRole("ADMIN")
                                .requestMatchers("/promotions/api/{id}/**").hasRole("ADMIN")
                                .requestMatchers("/promotions/api/validate-coupon").hasRole("ADMIN")
                                .requestMatchers("/promotions/api/record-usage").hasRole("ADMIN")
                                .requestMatchers("/promotions/api/assign-to-user").hasRole("ADMIN")
                                .requestMatchers("/promotions/api/user/{userId}").hasRole("ADMIN")
                                .requestMatchers("/promotions/api/user/{userId}/available").hasRole("ADMIN")

                                .anyRequest().permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/auth/login");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setHeader("Set-Cookie", "jwtToken=; Path=/; HttpOnly; Max-Age=0; Secure");
                            response.sendRedirect("/auth/login");
                        })
                        .permitAll()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}