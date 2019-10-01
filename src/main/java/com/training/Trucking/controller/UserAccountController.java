package com.training.Trucking.controller;

import com.training.Trucking.dto.OrderDTO;
import com.training.Trucking.entity.Order;
import com.training.Trucking.entity.Route;
import com.training.Trucking.entity.User;
import com.training.Trucking.entity.enumeration.CargoType;
import com.training.Trucking.service.OrderService;
import com.training.Trucking.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class UserAccountController {

    private final UserService userService;

    private final OrderService orderService;

    public UserAccountController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping(value = "/user/personal-cabinet")
    public String getUserPage(Model model, @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        model.addAttribute("currentPage", currentPage);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName()).orElseThrow(RuntimeException::new);
        model.addAttribute("user", user);

        Page<Order> orderPage = orderService.findPaginated(PageRequest.of(currentPage - 1, pageSize), user);

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("maxPage", orderPage.getTotalPages());

        int totalPages = orderPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "user-cabinet.html";
    }

    @GetMapping(value = "/user/place-order")
    public String getOrderPage(Model model, OrderDTO orderDTO, @RequestParam(value = "from",required = false) String from,
                               @RequestParam(value = "to", required = false) String to,
                               @RequestParam(value = "weight", required = false) Integer weight) {
        ArrayList<Route> routes = new ArrayList<>(orderService.getAllRoutes());

        Set<String> cities = routes.stream()
                .map(Route::getStartCity)
                .collect(Collectors.toSet());

        cities.addAll(routes.stream().map(Route::getEndCity).collect(Collectors.toSet()));

        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("weight", weight);
        model.addAttribute("cities", new ArrayList<>(cities));
        model.addAttribute("endCities", new ArrayList<>(cities).stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList()));

        model.addAttribute("cargoTypes", CargoType.values());
        return "user-order.html";
    }

    @PostMapping(value = "/user/place-order")
    public String placeOrder(Model model, @Valid OrderDTO orderDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/user/place-order";
        }
        orderService.saveOrder(orderDTO);
        ;
        return "redirect:/user/personal-cabinet";
    }

    @GetMapping(value = "/user/personal-cabinet/pay")
    public String rejectOrder(@RequestParam("id") long id) {
        Order order = orderService.findOrderById(id)
                .orElseThrow(RuntimeException::new);
        order.setPaid(true);
        orderService.saveOrder(order);
        return "redirect:/user/personal-cabinet";
    }

}
