package com.example.demo.controller;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void init(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController,"itemRepository",itemRepository);
    }

    @Test
    public void getItems_return_null(){

        when(itemRepository.findAll()).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(CollectionUtils.isEmpty(response.getBody()));
    }

    @Test
    public void getItemById_return_null(){

        Long id = 1L;
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() == null);
    }

    @Test
    public void getItemsByName_return_null_thenResponse_404(){

        String name = "testName";
        when(itemRepository.findByName(name)).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName(name);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getItemsByName_return_list_thenResponse_200(){

        String name = "testName";
        List<Item> items = new ArrayList<>();
        items.add(new Item());
        when(itemRepository.findByName(name)).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName(name);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
