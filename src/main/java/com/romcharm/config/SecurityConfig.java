package com.romcharm.config;

import com.romcharm.authorization.JWTAuthenticationFilter;
import com.romcharm.authorization.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTUtil jwtUtil;

    @Autowired
    public SecurityConfig(@SuppressWarnings("SpringJavaAutowiringInspection") JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    JWTAuthenticationFilter getJwtAuthenticationFilter() {
        return new JWTAuthenticationFilter(jwtUtil);
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        // Ignore Security filters on these
        webSecurity.ignoring().antMatchers("/users/auth", "/health")
                                // Ignore Swagger related stuff
                              .antMatchers("/v2/api-docs",
                                      "/configuration/ui",
                                      "/swagger-resources",
                                      "/configuration/security",
                                      "/swagger-ui.html",
                                      "/webjars/**")
                              .antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/users/add", "/projects/add").hasRole("ADMIN")
                .antMatchers("/families/**").hasRole("ROMCHARM_APP")
                .antMatchers("/projects", "/projects/**").hasRole("MYPAGE_APP")
                .anyRequest().authenticated()
            .and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(getJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
