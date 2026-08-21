// Digital Banking System - JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Format currency inputs
    const currencyInputs = document.querySelectorAll('input[type="number"][step="0.01"]');
    currencyInputs.forEach(function(input) {
        input.addEventListener('blur', function() {
            if (this.value) {
                this.value = parseFloat(this.value).toFixed(2);
            }
        });
    });

    // Confirm before form submission for important actions
    const confirmForms = document.querySelectorAll('[data-confirm]');
    confirmForms.forEach(function(form) {
        form.addEventListener('submit', function(e) {
            const message = this.getAttribute('data-confirm');
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });

    // Toggle password visibility
    const togglePasswordBtns = document.querySelectorAll('.toggle-password');
    togglePasswordBtns.forEach(function(btn) {
        btn.addEventListener('click', function() {
            const input = document.querySelector(this.getAttribute('data-target'));
            if (input.type === 'password') {
                input.type = 'text';
                this.innerHTML = '<i class="bi bi-eye-slash"></i>';
            } else {
                input.type = 'password';
                this.innerHTML = '<i class="bi bi-eye"></i>';
            }
        });
    });

    // Real-time balance validation for withdrawals
    const withdrawForm = document.querySelector('form[action*="/withdraw"]');
    if (withdrawForm) {
        const accountSelect = withdrawForm.querySelector('select[name="accountNumber"]');
        const amountInput = withdrawForm.querySelector('input[name="amount"]');

        if (accountSelect && amountInput) {
            amountInput.addEventListener('input', function() {
                const selectedOption = accountSelect.options[accountSelect.selectedIndex];
                if (selectedOption && selectedOption.value) {
                    const balanceMatch = selectedOption.text.match(/\$([0-9,.]+)\)/);
                    if (balanceMatch) {
                        const balance = parseFloat(balanceMatch[1].replace(',', ''));
                        const amount = parseFloat(this.value) || 0;
                        if (amount > balance) {
                            this.setCustomValidity('Amount exceeds available balance');
                        } else {
                            this.setCustomValidity('');
                        }
                    }
                }
            });
        }
    }

    // Prevent same account selection in transfer form
    const transferForm = document.querySelector('form[action*="/transfer"]');
    if (transferForm) {
        const fromAccount = transferForm.querySelector('select[name="fromAccountNumber"]');
        const toAccount = transferForm.querySelector('input[name="toAccountNumber"]');

        if (fromAccount && toAccount) {
            transferForm.addEventListener('submit', function(e) {
                if (fromAccount.value === toAccount.value) {
                    e.preventDefault();
                    alert('Source and destination accounts cannot be the same');
                }
            });
        }
    }

    // Add loading state to forms
    const forms = document.querySelectorAll('form');
    forms.forEach(function(form) {
        form.addEventListener('submit', function() {
            const submitBtn = this.querySelector('button[type="submit"]');
            if (submitBtn && !submitBtn.disabled) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span> Processing...';
            }
        });
    });

    // Initialize tooltips
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipTriggerList.forEach(function(tooltipTriggerEl) {
        new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Copy account number to clipboard
    const copyBtns = document.querySelectorAll('.copy-account');
    copyBtns.forEach(function(btn) {
        btn.addEventListener('click', function() {
            const accountNumber = this.getAttribute('data-account');
            navigator.clipboard.writeText(accountNumber).then(function() {
                const originalText = btn.innerHTML;
                btn.innerHTML = '<i class="bi bi-check"></i> Copied!';
                setTimeout(function() {
                    btn.innerHTML = originalText;
                }, 2000);
            });
        });
    });
});
