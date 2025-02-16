/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.SWP391.G3PCoffee.controller;

/**
 *
 * @author Anh
 */
import com.SWP391.G3PCoffee.model.Store;
import com.SWP391.G3PCoffee.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    // Get all stores
    @GetMapping
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    // Get a specific store by ID
    @GetMapping("/{id}")
    public Store getStoreById(@PathVariable Long id) {
        return storeService.getStoreById(id);
    }
}

