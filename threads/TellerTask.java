package threads;

import accounts.BankAccount;
import bank.Bank;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;

import java.util.List;
import java.util.Random;

// Implements Runnable for thread execution
public class TellerTask implements Runnable {

    private Bank bank;
    private String tellerName;
    private List<String> accountIds;
    private Random random = new Random();

    public TellerTask(Bank bank, String tellerName, List<String> accountIds) {
        this.bank = bank;
        this.tellerName = tellerName;
        this.accountIds = accountIds;
    }

    @Override
    public void run() {
        System.out.println(tellerName + " started.");

        for (int i = 0; i < 5; i++) {
            String accountId = accountIds.get(random.nextInt(accountIds.size()));
            double amount = 100 + random.nextInt(400); // ₹100 to ₹500
            boolean isDeposit = random.nextBoolean();

            try {
                BankAccount account = bank.getAccount(accountId);

                if (isDeposit) {
                    account.deposit(amount);
                } else {
                    account.withdraw(amount);
                }

            } catch (AccountNotFoundException e) {
                System.out.println(tellerName + " ERROR: " + e.getMessage());
            } catch (InsufficientFundsException e) {
                System.out.println(tellerName + " SKIPPED: " + e.getMessage());
            } catch (InvalidAmountException e) {
                System.out.println(tellerName + " INVALID: " + e.getMessage());
            }

            // Small delay to increase chance of thread interleaving
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println(tellerName + " finished.");
    }
}
