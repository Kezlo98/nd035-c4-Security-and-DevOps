package com.example.demo.security;

import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JWTAuthenticationFilterTest {

    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    ObjectMapper objectMapper = mock(ObjectMapper.class);

    JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager,objectMapper);

    @Test
    public void attemptAuthentication_throwException() throws IOException {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getInputStream()).thenThrow(new  IOException());

        try{
            jwtAuthenticationFilter.attemptAuthentication(request,response);
            assertTrue(false);
        }catch (RuntimeException e){
            assertTrue(true);
        }
    }

    @Test
    public void attemptAuthentication_returnAuthentication() throws IOException {

        ServletInputStream servletInputStream = mock(ServletInputStream.class);

        User user = new User();
        user.setUsername("testUsername");
        user.setPassword("testPassword");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getInputStream()).thenReturn(servletInputStream);

        when(objectMapper.readValue(servletInputStream, User.class)).thenReturn(user);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);

        try{
            Authentication authentication = jwtAuthenticationFilter.attemptAuthentication(request, response);
            assertTrue(authentication == null);
        }catch (RuntimeException e){
            e.printStackTrace();
            assertTrue(true);
        }
    }

    @Test
    public void successfulAuthentication() throws ServletException, IOException {

        Authentication authentication = mock(Authentication.class);
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User("testUserName","testPassword", Collections.emptyList());

        HttpServletResponse response = mock(HttpServletResponse.class);

        when(authentication.getPrincipal()).thenReturn(user);

        jwtAuthenticationFilter.successfulAuthentication(null,response,null,authentication);
    }
}
