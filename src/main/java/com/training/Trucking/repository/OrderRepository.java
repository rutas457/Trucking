package com.training.Trucking.repository;

import com.training.Trucking.entity.Order;
import com.training.Trucking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findById(Long id);

    List<Order> findAllByUser(User user);
}
