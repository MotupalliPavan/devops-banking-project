package com.digitalbanking.digital_banking_system.controller.web;

import com.digitalbanking.digital_banking_system.dto.request.DepositRequest;
import com.digitalbanking.digital_banking_system.dto.request.TransferRequest;
import com.digitalbanking.digital_banking_system.dto.request.WithdrawRequest;
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
@RequestMapping("/transactions")
public class TransactionWebController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransactionWebController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    @GetMapping
    public String transactionHistory(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<TransactionDTO> transactions = transactionService
                .getCurrentUserTransactions(PageRequest.of(page, 15));
        model.addAttribute("transactions", transactions);
        return "transactions/history";
    }

    @GetMapping("/transfer")
    public String showTransferForm(Model model) {
        List<AccountDTO> accounts = accountService.getCurrentUserAccounts();
        model.addAttribute("accounts", accounts);
        return "transactions/transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@ModelAttribute TransferRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            TransactionDTO transaction = transactionService.transfer(request);
            redirectAttributes.addFlashAttribute("success",
                    "Transfer successful! Reference: " + transaction.getReferenceId());
            return "redirect:/transactions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/transactions/transfer";
        }
    }

    @GetMapping("/deposit")
    public String showDepositForm(Model model) {
        List<AccountDTO> accounts = accountService.getCurrentUserAccounts();
        model.addAttribute("accounts", accounts);
        return "transactions/deposit";
    }

    @PostMapping("/deposit")
    public String deposit(@ModelAttribute DepositRequest request,
                          RedirectAttributes redirectAttributes) {
        try {
            TransactionDTO transaction = transactionService.deposit(request);
            redirectAttributes.addFlashAttribute("success",
                    "Deposit successful! Reference: " + transaction.getReferenceId());
            return "redirect:/transactions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/transactions/deposit";
        }
    }

    @GetMapping("/withdraw")
    public String showWithdrawForm(Model model) {
        List<AccountDTO> accounts = accountService.getCurrentUserAccounts();
        model.addAttribute("accounts", accounts);
        return "transactions/withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(@ModelAttribute WithdrawRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            TransactionDTO transaction = transactionService.withdraw(request);
            redirectAttributes.addFlashAttribute("success",
                    "Withdrawal successful! Reference: " + transaction.getReferenceId());
            return "redirect:/transactions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/transactions/withdraw";
        }
    }
}
