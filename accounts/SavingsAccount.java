package accounts;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;

// Inheritance: SavingsAccount extends BankAccount
public class SavingsAccount extends BankAccount {

    private double interestRate;
    private static final double MINIMUM_BALANCE = 500.0;

    public SavingsAccount(String accountId, String ownerName,
                          double initialBalance, double interestRate) {
        super(accountId, ownerName, initialBalance);
        this.interestRate = interestRate;
    }

    // Method Overriding: blocks withdrawals below minimum balance
    @Override
    public synchronized void withdraw(double amount)
            throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        if ((balance - amount) < MINIMUM_BALANCE) {
            throw new InsufficientFundsException(amount);
        }
        balance -= amount;
        System.out.printf("[%s] Withdrew ₹%.2f | New Balance: ₹%.2f (min ₹%.2f maintained)%n",
                getAccountId(), amount, balance, MINIMUM_BALANCE);
    }

    // Extra behavior specific to SavingsAccount
    public synchronized void applyInterest() {
        double interest = balance * (interestRate / 100);
        balance += interest;
        System.out.printf("[%s] Interest Applied @ %.1f%% | +₹%.2f | New Balance: ₹%.2f%n",
                getAccountId(), interestRate, interest, balance);
    }

    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public String toString() {
        return String.format("SavingsAccount[%s | Owner: %s | Balance: ₹%.2f | Rate: %.1f%%]",
                getAccountId(), getOwnerName(), balance, interestRate);
    }
}
