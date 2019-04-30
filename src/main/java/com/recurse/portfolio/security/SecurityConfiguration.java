package com.recurse.portfolio.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private PortfolioOAuth2UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requiresChannel()
            .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
            .requiresSecure()
            .and()
            .authorizeRequests()
            .regexMatchers(".*\\?login$").authenticated()
            .and()
            .oauth2Login()
            .userInfoEndpoint()
            .userService(userService)
        ;
    }
}
