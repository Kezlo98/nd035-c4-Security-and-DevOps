package com.example.demo.controller;


import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before()
    public void init(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController,"userRepository",userRepository);
        TestUtils.injectObjects(cartController,"cartRepository",cartRepository);
        TestUtils.injectObjects(cartController,"itemRepository",itemRepository);
    }

    @Test
    public void addTocart_user_null_return_404(){

        ModifyCartRequest request = createModifyCartRequest();

        when(userRepository.findByUsername(request.getUsername())).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void addTocart_returnUser_returnNoItem_response_404(){
        ModifyCartRequest request = createModifyCartRequest();
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Mockito.mock(User.class));
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void addTocart_returnUser_returnItem_saveCartSuccess_return_200(){

        User user = createUser();
        Item item = createItem();

        ModifyCartRequest request = createModifyCartRequest();
        when(userRepository.findByUsername(request.getUsername())).thenReturn(user);
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        Cart responseCart = response.getBody();
        assert responseCart != null;
        assertEquals(1L, responseCart.getId().longValue());
        assertTrue(!CollectionUtils.isEmpty(responseCart.getItems()));
        assertEquals(2,responseCart.getItems().size());
    }

    @Test
    public void removeFromcart_user_null_return_404(){

        ModifyCartRequest request = createModifyCartRequest();

        when(userRepository.findByUsername(request.getUsername())).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void removeFromcart_returnUser_returnNoItem_response_404(){
        ModifyCartRequest request = createModifyCartRequest();
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Mockito.mock(User.class));
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void removeFromcart_returnUser_returnItem_saveCartSuccess_return_200(){

        User user = createUser();
        Item item = createItem();

        ModifyCartRequest request = createModifyCartRequest();
        when(userRepository.findByUsername(request.getUsername())).thenReturn(user);
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        Cart responseCart = response.getBody();
        assert responseCart != null;
        assertEquals(1L, responseCart.getId().longValue());
        assertTrue(CollectionUtils.isEmpty(responseCart.getItems()));
    }

    public ModifyCartRequest createModifyCartRequest(){
        ModifyCartRequest request = new ModifyCartRequest();

        request.setUsername("testUserName");
        request.setItemId(1L);
        request.setQuantity(1);

        return request;
    }

    public User createUser(){
        User user = new User();
        Cart cart = new Cart();

        cart.setId(1L);
//        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        cart.getItems().add(createItem());

        user.setUsername("testUserName");
        user.setPassword("testPassword");
        user.setId(1);
        user.setCart(cart);

        return user;
    }

    private Item createItem(){
        Item item = new Item();

        item.setId(1L);
        item.setDescription("testingDescription");
        item.setName("testingName");
        item.setPrice(BigDecimal.TEN);

        return item;
    }
}
