package com.training.Trucking.controller;

import com.training.Trucking.dto.UserDTO;
import com.training.Trucking.entity.ConfirmationToken;
import com.training.Trucking.entity.User;
import com.training.Trucking.service.ConfirmationTokenService;
import com.training.Trucking.service.EmailSenderService;
import com.training.Trucking.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Controller
public class RegistrationController {

    private final UserService userService;

    private final ConfirmationTokenService confirmationTokenService;

    private final EmailSenderService emailSenderService;

    public RegistrationController(UserService userService, ConfirmationTokenService confirmationTokenService, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("/reg")
    public String getRegistrationPage(UserDTO userDTO) {
        return "reg.html";
    }

    @PostMapping("/reg")
    public String validateRegistrationInfo(Model model, @Valid UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "reg.html";
        }
        userService.saveUser(userDTO);

        ConfirmationToken confirmationToken = new ConfirmationToken(userService.findByEmail(userDTO.getEmail()).get());
        confirmationTokenService.save(confirmationToken);
        emailSenderService.sendEmail(userDTO.getEmail(), confirmationToken);

        return "account-need-verification.html";
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(Model model, @RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenService.confirmEmail(confirmationToken);
        if (!token.isExpired()) {
            User user = userService.findByEmail(token.getUser().getEmail()).orElseThrow(
                    () -> new UsernameNotFoundException("user " + token.getUser().getEmail() + " was not found!")
            );
            user.setEnabled(true);
            userService.saveUser(user);
            return "account-verified.html";
        } else {
            return "redirect:/confirm-account/resend?token=" + confirmationToken;
        }
    }

    @RequestMapping(value = "confirm-account/resend", method = RequestMethod.GET)
    public String showResendConfirmToken() {
        return "account-token-rejected";
    }

    @PostMapping(value = "confirm-account/resend")
    public String resendConfirmationToken(@RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenService.findByConfirmationToken(confirmationToken)
                .orElseThrow(() -> new UsernameNotFoundException("No such token =" + confirmationToken));
        token.setConfirmationToken(UUID.randomUUID().toString());
        token.setExpiryDate(token.calculateExpiryDate());
        confirmationTokenService.save(token);
        emailSenderService.sendEmail(token.getUser().getEmail(), token);
        return "redirect:/login";
    }
}


