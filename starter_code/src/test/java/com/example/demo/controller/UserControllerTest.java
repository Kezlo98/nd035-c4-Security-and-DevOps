package com.example.demo.controller;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
public class UserControllerTest {

    UserController userController;
    UserRepository userRepository = mock(UserRepository.class);
    CartRepository cartRepository = mock(CartRepository.class);
    BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void init(){
        userController = new UserController();
        TestUtils.injectObjects(userController,"userRepository",userRepository);
        TestUtils.injectObjects(userController,"userRepository",userRepository);
        TestUtils.injectObjects(userController,"cartRepository",cartRepository);
        TestUtils.injectObjects(userController,"bCryptPasswordEncoder",bCryptPasswordEncoder);
    }

    @Test
    public void findById_return_null_response_404(){

        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<User> responseEntity = userController.findById(id);
        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
    }

    @Test
    public void findByUserName_notFound_User_response_404(){

        String username = "testUsername";
        when(userRepository.findByUsername(username)).thenReturn(null);

        ResponseEntity<User> responseEntity = userController.findByUserName(username);
        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
    }

    @Test
    public void findByUserName_found_user_response_202(){

        String username = "testUsername";
        when(userRepository.findByUsername(username)).thenReturn(new User());

        ResponseEntity<User> responseEntity = userController.findByUserName(username);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void createUser_passwordLength_lessThan7_return_400(){
        CreateUserRequest request = new CreateUserRequest();

        request.setUsername("testUsername");
        request.setPassword("haha");
        request.setConfirmPassword("confirmPasswordTest");

        ResponseEntity<User> response = userController.createUser(request);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void createUser_password_and_confirmPassword_notMatch_return_400(){
        CreateUserRequest request = new CreateUserRequest();

        request.setUsername("testUsername");
        request.setPassword("passwordTest");
        request.setConfirmPassword("confirmPasswordTest");

        ResponseEntity<User> response = userController.createUser(request);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void createUser_success_return_200_and_user(){
        CreateUserRequest request = new CreateUserRequest();

        String encriptedPassword = "asldjasdiwvxpwqe12lkr283490r";

        request.setUsername("testUsername");
        request.setPassword("passwordTest");
        request.setConfirmPassword("passwordTest");

        when(bCryptPasswordEncoder.encode(request.getPassword())).thenReturn(encriptedPassword);
        when(cartRepository.save(any(Cart.class))).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(null);

        ResponseEntity<User> response = userController.createUser(request);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(request.getUsername(), response.getBody().getUsername());
        assertEquals(encriptedPassword, response.getBody().getPassword());
    }

}
