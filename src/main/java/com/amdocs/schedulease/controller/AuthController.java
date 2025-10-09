package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ========== SIGNUP ==========

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String registerUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            RedirectAttributes redirectAttributes) {

        // Validate passwords match
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match");
            return "redirect:/auth/signup";
        }

        // Validate password strength (minimum 8 characters)
        if (password.length() < 8) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 8 characters long");
            return "redirect:/auth/signup";
        }

        try {
            authService.registerUser(email, password, fullName, phone);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registration successful! Your account is pending approval. You will be notified once approved.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/signup";
        }
    }

    // ========== LOGIN ==========

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            UserAccount user = authService.login(email, password);

            // Store user in session
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRoles().iterator().next().getName().toString());

            // Redirect based on role
            String role = session.getAttribute("userRole").toString();
            switch (role) {
                case "ADMIN":
                    return "redirect:/admin/dashboard";
                case "STAFF":
                    return "redirect:/staff/dashboard";
                case "USER":
                default:
                    return "redirect:/user/dashboard";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // ========== LOGOUT ==========

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully");
        return "redirect:/auth/login";
    }

    // ========== PASSWORD RESET ==========

    @GetMapping("/password-reset")
    public String showPasswordResetForm(Model model) {
        return "auth/password-reset";
    }

    @PostMapping("/password-reset")
    public String requestPasswordReset(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        try {
            // Check if email exists
            if (!authService.emailExists(email)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email not found");
                return "redirect:/auth/password-reset";
            }

            // TODO: Implement email sending logic here
            // For now, just show success message
            redirectAttributes.addFlashAttribute("successMessage", 
                "Password reset link has been sent to your email");
            return "redirect:/auth/password-reset-success";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/password-reset";
        }
    }

    @GetMapping("/password-reset-success")
    public String showPasswordResetSuccess(Model model) {
        return "auth/password-reset-success";
    }
}
