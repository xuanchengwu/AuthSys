package config;


import Security.Base64AuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class SecurityConfig{

    @Bean
    public Base64AuthenticationFilter base64AuthenticationFilter() {
        return new Base64AuthenticationFilter();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, Base64AuthenticationFilter base64AuthenticationFilter) throws Exception {
        http.addFilterBefore(base64AuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http
                .csrf(AbstractHttpConfigurer::disable) // Disabling CSRF protection
                .addFilterBefore(base64AuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/admin/addUser").hasRole("ADMIN")// Only admins can access /admin/addUser
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "USER") // Both admins and users can access /admin/**
                                .requestMatchers("/user/**").hasRole("USER")
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());


        return http.build();
    }
}
