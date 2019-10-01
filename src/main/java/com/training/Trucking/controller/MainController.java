package com.training.Trucking.controller;

import com.training.Trucking.entity.enumeration.CargoType;
import com.training.Trucking.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    private final OrderService orderService;

    public MainController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/")
    public String getIndexPage() {
        return "index.html";
    }

    @PostMapping(value = "/")
    public String calculationForGuest(@RequestParam("from") String fromCity, @RequestParam("to") String toCity,
                                      @RequestParam("weight") Integer weight, Model model) {
        model.addAttribute("firstCity", fromCity);
        model.addAttribute("secondCity", toCity);
        if (weight == null) {
            return "index.html";
        }
        model.addAttribute("weight", weight);
        model.addAttribute("price", orderService.getPrice(fromCity, toCity, weight, CargoType.STANDARD));

        return "guest-calculate.html";
    }

    @GetMapping(value = "/user")
    public String userResolve() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:admin/cabinet";
        } else {
            return "redirect:user/personal-cabinet";
        }
    }

}
