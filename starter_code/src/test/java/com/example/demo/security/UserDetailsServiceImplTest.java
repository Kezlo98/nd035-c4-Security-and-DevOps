package com.example.demo.security;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDetailsServiceImplTest {

    UserDetailsService userDetailsService;
    UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void init(){
        userDetailsService = new UserDetailsServiceImpl();
        TestUtils.injectObjects(userDetailsService,"userRepository",userRepository);
    }

    @Test
    public void loadUserByUserName_notFound_user_throw_exception(){

        when(userRepository.findByUsername(anyString())).thenReturn(null);

        try{
            userDetailsService.loadUserByUsername("Test");
            assertTrue(false);
        }catch (UsernameNotFoundException exception){
            assertTrue(true);
        }
    }

    @Test
    public void loadUserByUserName_found_user_return_User(){

        User user = new User();
        user.setUsername("testUserName");
        user.setPassword("testPassword");

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        try{
            UserDetails userDetails = userDetailsService.loadUserByUsername("Test");
            assertEquals(user.getUsername(),userDetails.getUsername());
            assertEquals(user.getPassword(),userDetails.getPassword());
        }catch (UsernameNotFoundException exception){
            assertTrue(false);
        }
    }

}
