package com.test.lsy.jwt1.config;

import com.test.lsy.jwt1.filter.MyFilter1;
import com.test.lsy.jwt1.filter.MyFilter3;
import com.test.lsy.jwt1.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    private final CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class);
        http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // cors 허용하기 위해 필터에 등록
                .addFilter(corsFilter)          // @CrosOrign 사용은 인증이 없을 때, 인증이 있을 때는 필터에 등록을 해야 함
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                // 필터 동작하도록 등록
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/v1/user/**")
                                .hasAnyRole("USER", "MANAGER", "ADMIN")
                                .requestMatchers("/api/v1/manager/**")
                                .hasAnyRole("MANAGER", "ADMIN")
                                .requestMatchers("/api/v1/admin/**")
                                .hasAnyRole("ADMIN")
                                .anyRequest().permitAll()
                );
        return http.build();
    }
}
