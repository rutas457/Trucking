package com.training.Trucking.service;


import com.training.Trucking.dto.UserDTO;
import com.training.Trucking.dto.UsersDTO;
import com.training.Trucking.entity.Role;
import com.training.Trucking.entity.User;
import com.training.Trucking.entity.UserAccount;
import com.training.Trucking.repository.UserRepository;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UsersDTO getAllUsers() {
        return new UsersDTO(userRepository.findAll());
    }

    public Optional<User> findById(@NonNull Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(@NonNull String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(UserDTO userDTO) {

        User user = User.builder().email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .isBanned(false)
                .enabled(false)
                .firstName(userDTO.getName())
                .surname(userDTO.getSurname())
                .userAccount(new UserAccount())
                .roles(Arrays.asList(new Role("ROLE_USER"))).build();
        try {
            userRepository.save(user);
        } catch (Exception ex) {
            log.info("{Почтовый адрес уже существует}");
        }
    }

    public void saveUser(User user) {
        try {
            userRepository.save(user);
        } catch (Exception ex) {
            log.info("{Почтовый адрес уже существует}");
        }
    }

    public void updatePassword(UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() ->
                new UsernameNotFoundException("user " + userDTO.getEmail() + " was not found!"));
        userRepository.updatePasswordById(passwordEncoder.encode(userDTO.getPassword()), user.getId());
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("user " + username + " was not found!"));


        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                user.getEnabled(),
                true,
                true,
                !user.getIsBanned(),
                mapRolesToAuthorities(user.getRoles()));
    }


    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
