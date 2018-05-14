package com.ttu.tarkvaratehnika.empires.gameofempires.security;

import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import com.ttu.tarkvaratehnika.empires.gameofempires.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class AuthTokenFilter extends OncePerRequestFilter {

    private TokenService tokenService;
    private AccountService accountService;

    private String tokenHeaderName = "authorization";

    public AuthTokenFilter(TokenService tokenService, AccountService accountService) {
        this.tokenService= tokenService;
        this.accountService = accountService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authToken = request.getHeader(tokenHeaderName);
        if (authToken != null) {
            Optional<String> username = tokenService.getUsernameFromToken(authToken);
            if (username.isPresent()) {
                Optional<User> optionalUser = accountService.getUserByUsername(username.get());
                if (optionalUser.isPresent()) {
                    Authentication authentication = new AuthenticationData(null, optionalUser.get());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
