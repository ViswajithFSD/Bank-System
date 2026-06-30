package accounts;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;

public class BankAccount {

    // Encapsulation: private fields
    private String accountId;
    private String ownerName;
    protected double balance;

    public BankAccount(String accountId, String ownerName, double initialBalance) {
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.balance = initialBalance;
    }

    // synchronized: only one thread can deposit at a time
    public synchronized void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        balance += amount;
        System.out.printf("[%s] Deposited ₹%.2f | New Balance: ₹%.2f%n",
                accountId, amount, balance);
    }

    // synchronized: only one thread can withdraw at a time
    public synchronized void withdraw(double amount)
            throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        if (amount > balance) {
            throw new InsufficientFundsException(amount);
        }
        balance -= amount;
        System.out.printf("[%s] Withdrew ₹%.2f | New Balance: ₹%.2f%n",
                accountId, amount, balance);
    }

    public synchronized double getBalance() {
        return balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public String toString() {
        return String.format("Account[%s | Owner: %s | Balance: ₹%.2f]",
                accountId, ownerName, balance);
    }
}
