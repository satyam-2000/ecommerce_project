package com.example.ecommerce.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.ecommerce.jwt.UserDetailsImpl;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;

@Component
public class AuthUtil {

    @Autowired
    private UserRepository userRepository;

    // public static Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userName = authentication.getName();
        User user = userRepository.findByUsername(userName);
        return user.getEmail();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());
        return user;
    }

}
