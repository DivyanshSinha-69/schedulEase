package com.amdocs.schedulease.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        // If user is already logged in, redirect to dashboard
        if (session.getAttribute("user") != null) {
            String role = (String) session.getAttribute("userRole");
            
            if ("ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            } else if ("STAFF".equals(role)) {
                return "redirect:/staff/dashboard";
            } else {
                return "redirect:/user/dashboard";
            }
        }
        
        // Otherwise show landing page
        return "index";
    }
}
