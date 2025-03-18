package com.example.ecommerce.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.config.AppRole;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.LoginResponse;
import com.example.ecommerce.dto.MessageResponse;
import com.example.ecommerce.dto.SignupRequest;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.jwt.JwtUtil;
import com.example.ecommerce.jwt.UserDetailsImpl;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            ResponseCookie cookie = jwtUtil.generateJwtCookie(userDetails);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            List<String> roles = userDetails.getAuthorities().stream().map(authority -> authority.getAuthority())
                    .collect(Collectors.toList());
            LoginResponse loginResponse = new LoginResponse(userDetails.getUsername(), roles);
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(loginResponse);
        } catch (Exception e) {
            // TODO: handle exception
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad Credentials");
            map.put("Status", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    @GetMapping("/sign-out")
    public ResponseEntity<?> signout() {
        ResponseCookie cookie = jwtUtil.cleanCookie();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Successfully Sign out!!");
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            MessageResponse messageResponse = new MessageResponse("Username already Exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageResponse);
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            MessageResponse messageResponse = new MessageResponse("Email already Exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageResponse);
        }

        // Create user;
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        // Roles related things
        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role role = null;
            if (roleRepository.existsByRoleName(AppRole.ROLE_USER)) {
                role = roleRepository.findByRoleName(AppRole.ROLE_USER).orElse(null);
            } else {
                Role role2 = new Role();
                role2.setRoleName(AppRole.ROLE_USER);
                role = roleRepository.save(role2);
            }
            roles.add(role);
        }

        else {
            strRoles.forEach((e) -> {
                Role role = null;
                switch (e.toLowerCase()) {
                    case "admin":
                        if (roleRepository.existsByRoleName(AppRole.ROLE_ADMIN)) {
                            role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElse(null);
                        } else {
                            Role role2 = new Role();
                            role2.setRoleName(AppRole.ROLE_ADMIN);
                            role = roleRepository.save(role2);
                        }
                        roles.add(role);
                        break;
                    case "seller":
                        if (roleRepository.existsByRoleName(AppRole.ROLE_SELLER)) {
                            role = roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElse(null);
                        } else {
                            Role role2 = new Role();
                            role2.setRoleName(AppRole.ROLE_SELLER);
                            role = roleRepository.save(role2);
                        }
                        roles.add(role);
                        break;
                    default:
                        if (roleRepository.existsByRoleName(AppRole.ROLE_USER)) {
                            role = roleRepository.findByRoleName(AppRole.ROLE_USER).orElse(null);
                        } else {
                            Role role2 = new Role();
                            role2.setRoleName(AppRole.ROLE_USER);
                            role = roleRepository.save(role2);
                        }
                        roles.add(role);
                        break;
                }
            });
        }
        // if (strRoles == null) {
        // Role role = new Role();
        // role.setRoleName(AppRole.ROLE_USER);
        // roles.add(role);
        // }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok().body("User Registered Successfully");

    }
}
