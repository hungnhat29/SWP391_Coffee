/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.SWP391.G3PCoffee.controller;

/**
 * @author hungp
 */
//import com.SWP391.G3PCoffee.dto.UserDTO;

import com.SWP391.G3PCoffee.service.OrderService;
import com.SWP391.G3PCoffee.service.UserService;
import com.SWP391.G3PCoffee.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/customers")
    public List<User> getCustomers() {
        return userService.getAllCustomers();
    }

    @GetMapping("/customers/{id}")
    public User getCustomerById(@PathVariable Long id) {
        User customer = userService.getCustomerById(id); // Assume this method exists
        if (customer != null) {
            long orderCount = orderService.getOrderCountByCustomerId(Math.toIntExact(customer.getId()));
            customer.setOrderCount(orderCount); // Assume User model has a setter for orderCount
        }
        return customer;
    }
}

