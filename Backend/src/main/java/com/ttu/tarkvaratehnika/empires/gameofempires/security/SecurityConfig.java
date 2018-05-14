package com.ttu.tarkvaratehnika.empires.gameofempires.security;

import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private TokenService tokenService;
    private AccountService accountService;

    public SecurityConfig(TokenService tokenService, AccountService accountService) {
        this.tokenService = tokenService;
        this.accountService = accountService;
    }

    @Bean
    public AuthTokenFilter authTokenFilterBean() throws Exception {
        return new AuthTokenFilter(tokenService, accountService);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity.addFilterBefore(authTokenFilterBean(), BasicAuthenticationFilter.class);
    }
}
