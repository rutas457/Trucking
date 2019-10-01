package com.training.Trucking.controller;

import com.training.Trucking.entity.Order;
import com.training.Trucking.entity.Role;
import com.training.Trucking.entity.User;
import com.training.Trucking.entity.enumeration.OrderStatus;
import com.training.Trucking.service.OrderService;
import com.training.Trucking.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@Slf4j
@Controller
public class AdminController {
    private final UserService userService;

    private final OrderService orderService;

    public AdminController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping(value = "/admin/cabinet")
    public String getAdminCabinet(Model model) {

        model.addAttribute("newOrders", orderService.getOrdersByStatus(OrderStatus.CREATED));

        model.addAttribute("ordersInProcess", orderService.getOrdersByStatus(OrderStatus.PROCESSING));

        model.addAttribute("completedOrders", orderService.getOrdersByStatus(OrderStatus.COMPLETED));

        model.addAttribute("rejectedOrders", orderService.getOrdersByStatus(OrderStatus.REJECTED));

        return "admin-orders.html";
    }

    @GetMapping(value = "/admin/cabinet/confirm")
    public String confirmOrder(@RequestParam("id") long id) {
        Order order = orderService.findOrderById(id)
                .orElseThrow(RuntimeException::new);
        order.setState(OrderStatus.PROCESSING);
        orderService.saveOrder(order);
        return "redirect:/admin/cabinet";
    }

    @GetMapping(value = "/admin/cabinet/complete")
    public String completeOrder(@RequestParam("id") long id) {
        Order order = orderService.findOrderById(id)
                .orElseThrow(RuntimeException::new);
        order.setState(OrderStatus.COMPLETED);
        orderService.saveOrder(order);
        return "redirect:/admin/cabinet";
    }

    @GetMapping(value = "/admin/cabinet/reject")
    public String rejectOrder(@RequestParam("id") long id) {
        Order order = orderService.findOrderById(id)
                .orElseThrow(RuntimeException::new);
        order.setState(OrderStatus.REJECTED);
        orderService.saveOrder(order);
        return "redirect:/admin/cabinet";
    }

    @GetMapping(value = "/admin/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers().getUsers());
        return "admin-users.html";
    }

    @GetMapping(value = "/admin/users/ban")
    public String userBan(@RequestParam("email") String email) {
        User user = userService.findByEmail(email).orElseThrow(RuntimeException::new);
        if (user.getRoles().stream().anyMatch(x -> x.getName().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/users";
        }
        user.setIsBanned(true);
        userService.saveUser(user);
        return "redirect:/admin/users";
    }


    @GetMapping(value = "/admin/users/promote")
    public String userPromote(@RequestParam("email") String email) {
        User user = userService.findByEmail(email).orElseThrow(RuntimeException::new);
        Collection<Role> roles = user.getRoles();
        roles.add(new Role("ROLE_ADMIN"));
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin/users";
    }
}
