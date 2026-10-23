
package com.example.orderflow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Indique que cette classe est une configuration Spring, ce qui permet à Spring de la détecter et de l'utiliser pour configurer la sécurité de l'application.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Autorise toutes les requêtes HTTP sans authentification, ce qui signifie que l'application n'applique pas de restrictions d'accès aux endpoints.
            )
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable());

        return http.build();
    }
}