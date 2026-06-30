package bank;

import accounts.BankAccount;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Bank {

    // Collections: HashMap to store accounts by ID
    private HashMap<String, BankAccount> accounts = new HashMap<>();

    // Collections: ArrayList as transaction log
    private ArrayList<String> transactionLog = new ArrayList<>();

    public void addAccount(BankAccount account) {
        accounts.put(account.getAccountId(), account);
        log("Account created: " + account);
    }

    public BankAccount getAccount(String accountId) throws AccountNotFoundException {
        BankAccount account = accounts.get(accountId);
        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }
        return account;
    }

    // Transfer: real exception handling challenge
    // If deposit fails after withdraw succeeds, we must rollback
    public synchronized void transfer(String fromId, String toId, double amount)
            throws AccountNotFoundException, InsufficientFundsException, InvalidAmountException {

        BankAccount from = getAccount(fromId);
        BankAccount to = getAccount(toId);

        // Withdraw first
        from.withdraw(amount);

        // Attempt deposit — if this fails, rollback the withdrawal
        try {
            to.deposit(amount);
            log(String.format("Transfer: ₹%.2f from [%s] to [%s]", amount, fromId, toId));
        } catch (InvalidAmountException e) {
            // Rollback: re-deposit back to sender
            from.deposit(amount);
            log("Transfer FAILED and rolled back: " + e.getMessage());
            throw e;
        }
    }

    public Collection<BankAccount> getAllAccounts() {
        return accounts.values();
    }

    public List<String> getTransactionLog() {
        return new ArrayList<>(transactionLog);
    }

    private synchronized void log(String message) {
        transactionLog.add("[LOG] " + message);
    }

    public void printSummary() {
        System.out.println("\n========== BANK SUMMARY ==========");
        for (BankAccount acc : accounts.values()) {
            System.out.println(acc);
        }
        System.out.println("\n--- Transaction Log ---");
        for (String entry : transactionLog) {
            System.out.println(entry);
        }
        System.out.println("===================================\n");
    }
}
