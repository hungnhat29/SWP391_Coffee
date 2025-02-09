/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.SWP391.G3PCoffee.service;

/**
 * @author hungp
 */
//import com.SWP391.G3PCoffee.dto.UserDTO;

import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //    public List<User> getAllCustomers() {
//        return userRepository.findAllCustomers();
//    }
    public List<User> getAllCustomers() {
        return userRepository.findAll().stream()
                .filter(user -> "customer".equalsIgnoreCase(user.getName()))
                .toList();
    }

    public User getCustomerById(Long id) {
        return userRepository.findById(id)
                .filter(user -> "customer".equalsIgnoreCase(user.getName()))
                .orElse(null);
    }

//    public User getCustomerById(Long id) {
//        return userRepository.findCustomerById(id);
//    }
}
