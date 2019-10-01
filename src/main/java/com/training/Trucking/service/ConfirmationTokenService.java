package com.training.Trucking.service;

import com.training.Trucking.entity.ConfirmationToken;
import com.training.Trucking.repository.ConfirmationTokenRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Getter
@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public ConfirmationToken confirmEmail(String confirmationToken) {
        return confirmationTokenRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(() -> new UsernameNotFoundException("token " + confirmationToken + " was not found!"));
    }

    public void save(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> findByConfirmationToken(String token) {
        return confirmationTokenRepository.findByConfirmationToken(token);
    }
}
