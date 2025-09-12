package com.specsShope.specsBackend.Config;

import com.specsShope.specsBackend.Services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register-admin").hasRole("ADMIN")
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/v3/api-docs/swagger-config").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // ✅ Allow USER to submit callback
                        .requestMatchers(HttpMethod.POST, "/api/callbacks/request").hasRole("USER")

                        // ✅ Allow ADMIN to view callback list
                        .requestMatchers(HttpMethod.GET, "/api/callbacks").hasRole("ADMIN")

                        // ✅ Allow USER to access cart APIs
                        .requestMatchers("/api/cart/**").hasRole("USER")

                        // ✅ Reviews (admin first, then user)
                        .requestMatchers("/api/reviews/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()

                        // ✅ Appointments
                        .requestMatchers(HttpMethod.POST, "/api/appointments/book").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/user/me").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/appointments/summary").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/available-slots").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/admin/appointments/by-reason").hasRole("ADMIN")

                        // ✅ Admin APIs
                        .requestMatchers("/api/admin/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/callbacks/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/vision-records/**").hasRole("ADMIN")
                        .requestMatchers("/api/eye-tests/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/eye-tests/external/**").hasRole("ADMIN")
                        .requestMatchers("/api/eye-tests/my-history").hasRole("USER")
                        .requestMatchers("/api/eye-tests/my-latest").hasRole("USER")
                        .requestMatchers("/api/admin/past-users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/doctor-appointments").hasRole("ADMIN")
                        .requestMatchers("/api/doctor-appointments/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/doctor-past-users/**").hasRole("ADMIN")
                        .requestMatchers("/test").permitAll()
                        .requestMatchers("/healthz").permitAll()



                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
