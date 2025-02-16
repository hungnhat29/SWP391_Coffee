/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.SWP391.G3PCoffee.service;

/**
 *
 * @author Anh
 */
import com.SWP391.G3PCoffee.model.Store;
import com.SWP391.G3PCoffee.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class StoreService {
    @Autowired
    private StoreRepository storeRepository;

    public List<Store> getAllStores() {
    return storeRepository.findAll();    
    }
    
    public List<Store> getStoreByLocation(String location) {
    return storeRepository.findByLocationIgnoreCase(location);
}
    
    public Store getStoreById(Long id) {
        return storeRepository.findById(id).orElse(null);
    }
}
