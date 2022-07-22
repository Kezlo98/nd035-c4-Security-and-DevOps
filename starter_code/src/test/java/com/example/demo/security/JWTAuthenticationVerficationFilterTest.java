package com.example.demo.security;

import com.auth0.jwt.JWT;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JWTAuthenticationVerficationFilterTest {

    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    JWTAuthenticationVerficationFilter jwtAuthenticationVerficationFilter = new JWTAuthenticationVerficationFilter(authenticationManager);

    @Test
    public void doFilterInternal_success() throws ServletException, IOException {

        String userName = "testUserName";

        String token = JWT.create()
                .withSubject(userName)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()));

        String header = "Bearer "+token;

        HttpServletRequest req = mock(HttpServletRequest.class);

        when(req.getHeader(SecurityConstants.HEADER_STRING)).thenReturn(header);

        jwtAuthenticationVerficationFilter.doFilterInternal(req,mock(HttpServletResponse.class),mock(FilterChain.class));
    }

    @Test
    public void doFilterInternal_header_null() throws ServletException, IOException {

        HttpServletRequest req = mock(HttpServletRequest.class);

        when(req.getHeader(SecurityConstants.HEADER_STRING)).thenReturn(null);

        jwtAuthenticationVerficationFilter.doFilterInternal(req,mock(HttpServletResponse.class),mock(FilterChain.class));
    }
}
