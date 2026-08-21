package com.digitalbanking.digital_banking_system.controller.web;

import com.digitalbanking.digital_banking_system.dto.request.ChangePasswordRequest;
import com.digitalbanking.digital_banking_system.dto.request.CreateUserRequest;
import com.digitalbanking.digital_banking_system.dto.response.AccountDTO;
import com.digitalbanking.digital_banking_system.dto.response.DashboardStatsDTO;
import com.digitalbanking.digital_banking_system.dto.response.TransactionDTO;
import com.digitalbanking.digital_banking_system.service.AccountService;
import com.digitalbanking.digital_banking_system.service.AuthService;
import com.digitalbanking.digital_banking_system.service.TransactionService;
import com.digitalbanking.digital_banking_system.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class HomeController {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final AuthService authService;

    public HomeController(UserService userService,
                          AccountService accountService,
                          TransactionService transactionService,
                          AuthService authService) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.authService = authService;
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute CreateUserRequest request,
                               RedirectAttributes redirectAttributes) {
        try {
            authService.register(request);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<AccountDTO> accounts = accountService.getCurrentUserAccounts();
        List<TransactionDTO> recentTransactions = transactionService
                .getCurrentUserTransactions(PageRequest.of(0, 5))
                .getContent();

        BigDecimal totalBalance = accounts.stream()
                .map(AccountDTO::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .totalAccounts(accounts.size())
                .totalBalance(totalBalance)
                .recentTransactionCount(recentTransactions.size())
                .build();

        model.addAttribute("user", userService.getCurrentUser());
        model.addAttribute("accounts", accounts);
        model.addAttribute("recentTransactions", recentTransactions);
        model.addAttribute("stats", stats);

        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        model.addAttribute("accounts", accountService.getCurrentUserAccounts());
        return "profile";
    }

    @GetMapping("/profile/change-password")
    public String showChangePasswordForm() {
        return "change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@ModelAttribute ChangePasswordRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.changePassword(request);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/change-password";
        }
    }
}
