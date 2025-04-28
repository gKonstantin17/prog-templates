package com.example.testauth2.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("jwt email = " + jwt.getClaim("email"));
        System.out.println("jwt = " + jwt);
        System.out.println("id delete = " + id);
        return "delete work";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('moderator')")
    public String add() {
        return "add work";
    }
}
