package com.MicroserviceP1.OrderService.Repository;

import com.MicroserviceP1.OrderService.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {

}
