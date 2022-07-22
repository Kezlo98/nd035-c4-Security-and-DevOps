package com.example.demo.controller;

import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void init() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submit_found_noUser_response_404() {

        String username = "testUsername";

        when(userRepository.findByUsername(username)).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit(username);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void submit_found_user_return_200(){

        User user = createUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.save(any(UserOrder.class))).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(!CollectionUtils.isEmpty(response.getBody().getItems()));
        assertEquals(BigDecimal.TEN,response.getBody().getTotal());
    }

    @Test
    public void getOrdersForUser_notFound_user_return_400(){

        String username = "testUsername";

        when(userRepository.findByUsername(username)).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getOrdersForUser_foundUser_return_200(){

        User user = createUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(any(User.class))).thenReturn(new ArrayList<>());

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private User createUser(){
        User user = new User();
        Cart cart = new Cart();
        Item item = new Item();

        item.setPrice(BigDecimal.TEN);
        item.setId(1L);
        item.setDescription("testDescription");
        item.setName("testName");

        cart.setItems(new ArrayList<>());
        cart.getItems().add(item);

        user.setCart(cart);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setId(1L);

        cart.setTotal(BigDecimal.TEN);
        cart.setUser(user);

        return user;
    }
}