package com.digitalbanking.digital_banking_system.controller.web;

import com.digitalbanking.digital_banking_system.dto.response.AccountDTO;
import com.digitalbanking.digital_banking_system.dto.response.TransactionDTO;
import com.digitalbanking.digital_banking_system.service.AccountService;
import com.digitalbanking.digital_banking_system.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/accounts")
public class AccountWebController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountWebController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public String listAccounts(Model model) {
        List<AccountDTO> accounts = accountService.getCurrentUserAccounts();
        model.addAttribute("accounts", accounts);
        return "accounts/list";
    }

    // Account creation is automatic during registration
    // Redirect any attempts to create account to accounts page
    @GetMapping("/create")
    public String showCreateForm(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("info", "Your account was created automatically during registration.");
        return "redirect:/accounts";
    }

    @GetMapping("/{accountNumber}")
    public String accountDetails(@PathVariable String accountNumber,
                                 @RequestParam(defaultValue = "0") int page,
                                 Model model) {
        AccountDTO account = accountService.getAccountByNumber(accountNumber);
        Page<TransactionDTO> transactions = transactionService
                .getTransactionsByAccountNumber(accountNumber, PageRequest.of(page, 10));

        model.addAttribute("account", account);
        model.addAttribute("transactions", transactions);
        return "accounts/details";
    }
}
