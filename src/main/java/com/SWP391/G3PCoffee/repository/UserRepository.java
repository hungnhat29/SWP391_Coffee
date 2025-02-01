/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.SWP391.G3PCoffee.repository;

/**
 *
 * @author hungp
 */
import com.SWP391.G3PCoffee.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//import org.springframework.data.jpa.repository.Query;
//import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    @Query("SELECT u FROM Users u WHERE u.role = 'customer'")
//    List<User> findAllCustomers();
//    
//    @Query("SELECT u FROM User u WHERE u.role = 'customer' AND u.id = :id")
//    User findCustomerById(Long id);
}