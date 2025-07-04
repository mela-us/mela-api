package com.hcmus.mela.auth.configuration;

import com.hcmus.mela.auth.security.jwt.JwtAccessDeniedHandler;
import com.hcmus.mela.auth.security.jwt.JwtAuthenticationEntryPoint;
import com.hcmus.mela.auth.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //@formatter:off
		return http
				.csrf(CsrfConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(request -> request.requestMatchers("/api/register",
																	      "/api/login",
																		  "/api/logout",
																		  "/api/forgot-password/**",
																		  "/api/refresh-token/**",
																	      "/v3/api-docs/**",
																          "/swagger-ui/**",
																	      "/swagger-ui.html",
																	      "/actuator/**")
													   .permitAll()
													   .anyRequest()
													   .authenticated())
				.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(handler -> handler
						.authenticationEntryPoint(unauthorizedHandler)
						.accessDeniedHandler(accessDeniedHandler))
				.build();
		//@formatter:on
    }

}
