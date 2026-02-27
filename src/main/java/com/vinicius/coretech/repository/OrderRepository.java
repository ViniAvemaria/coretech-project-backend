package com.vinicius.coretech.repository;

import com.vinicius.coretech.entity.Order;
import com.vinicius.coretech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
}
