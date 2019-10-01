package com.training.Trucking.service;

import com.training.Trucking.dto.OrderDTO;
import com.training.Trucking.dto.OrdersDTO;
import com.training.Trucking.entity.Order;
import com.training.Trucking.entity.Route;
import com.training.Trucking.entity.User;
import com.training.Trucking.entity.enumeration.CargoType;
import com.training.Trucking.entity.enumeration.OrderStatus;
import com.training.Trucking.repository.OrderRepository;
import com.training.Trucking.repository.RouteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final RouteRepository routeRepository;

    private final UserService userService;

    public OrderService(OrderRepository orderRepository, RouteRepository routeRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.routeRepository = routeRepository;
        this.userService = userService;
    }


    public Page<Order> findPaginated(Pageable pageable, User user) {
        List<Order> orders = getAllOrdersOfUser(user).getOrdersList().stream()
                .sorted(Comparator.comparingLong(Order::getId).reversed()).collect(Collectors.toList());

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Order> list;

        if (orders.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, orders.size());
            list = orders.subList(startItem, toIndex);
        }

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), orders.size());
    }

    public void saveOrder(OrderDTO orderDTO) {

        Order order = Order.builder()
                .cargoType(orderDTO.getCargoType())
                .cargoName(orderDTO.getCargoName())
                .state(OrderStatus.CREATED)
                .weight(orderDTO.getWeight())
                .route(routeRepository.findByStartCityAndEndCity(orderDTO.getToCity(), orderDTO.getFromCity())
                        .orElse(routeRepository.findByStartCityAndEndCity(orderDTO.getFromCity(), orderDTO.getToCity())
                                .orElseThrow(RuntimeException::new)))
                .shippingStart(LocalDate.parse(orderDTO.getShippingStart()).plusDays(1))
                .shippingEnd(LocalDate.parse(orderDTO.getShippingEnd()).plusDays(1))
                .paid(false)
                .build();
        order.setPrice(order.calculateOrderPrice());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        order.setUser(userService.findByEmail(auth.getName()).orElseThrow(RuntimeException::new));
        try {
            orderRepository.save(order);
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public List<Order> getOrdersByStatus(OrderStatus orderStatus) {
        return getAllUsersOrders()
                .getOrdersList()
                .stream()
                .filter(x -> x.getState().equals(orderStatus))
                .collect(Collectors.toList());
    }

    public long getPrice(String from, String to, int weight, CargoType cargoType) {
        log.info(from + " " + to);
        Route route = routeRepository.findByStartCityAndEndCity(to, from)
                .orElse(routeRepository.findByStartCityAndEndCity(from, to)
                        .orElseThrow(RuntimeException::new));

        Order order = Order.builder()
                .route(route)
                .weight(weight)
                .cargoType(cargoType)
                .build();
        return order.calculateOrderPrice();
    }

    public OrdersDTO getAllOrdersOfUser(User user) {
        return new OrdersDTO(orderRepository.findAllByUser(user));
    }

    public OrdersDTO getAllUsersOrders() {
        return new OrdersDTO(orderRepository.findAll());
    }

    public Optional<Order> findOrderById(long id) {
        return orderRepository.findById(id);
    }

    public Collection<Route> getAllRoutes() {
        return routeRepository.findAll();
    }
}
