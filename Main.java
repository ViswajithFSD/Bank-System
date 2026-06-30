import accounts.BankAccount;
import accounts.SavingsAccount;
import bank.Bank;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;
import threads.TellerTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    static Bank bank = new Bank();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========================================");
        System.out.println("   Welcome to Multi-Threaded Bank System");
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createAccount();
                case "2" -> deposit();
                case "3" -> withdraw();
                case "4" -> transfer();
                case "5" -> viewAllAccounts();
                case "6" -> viewTransactionLog();
                case "7" -> applyInterest();
                case "8" -> simulateTellers();
                case "9" -> { running = false; System.out.println("Goodbye!"); }
                default  -> System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
    }

    static void printMenu() {
        System.out.println("\n-------- MENU --------");
        System.out.println("1. Create Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer");
        System.out.println("5. View All Accounts");
        System.out.println("6. View Transaction Log");
        System.out.println("7. Apply Interest (Savings only)");
        System.out.println("8. Simulate Teller Threads");
        System.out.println("9. Exit");
        System.out.println("----------------------");
    }

    // ─── 1. Create Account ───────────────────────────────────────────────────
    static void createAccount() {
        System.out.print("Enter Account ID (e.g. ACC001): ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Owner Name: ");
        String name = scanner.nextLine().trim();

        double balance = readDouble("Enter Initial Balance: ");

        System.out.print("Account Type - (1) Regular  (2) Savings: ");
        String type = scanner.nextLine().trim();

        if (type.equals("2")) {
            double rate = readDouble("Enter Interest Rate (%): ");
            bank.addAccount(new SavingsAccount(id, name, balance, rate));
        } else {
            bank.addAccount(new BankAccount(id, name, balance));
        }

        System.out.println("Account created successfully.");
    }

    // ─── 2. Deposit ──────────────────────────────────────────────────────────
    static void deposit() {
        System.out.print("Enter Account ID: ");
        String id = scanner.nextLine().trim();
        double amount = readDouble("Enter Deposit Amount: ");

        try {
            BankAccount account = bank.getAccount(id);
            account.deposit(amount);
        } catch (AccountNotFoundException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    // ─── 3. Withdraw ─────────────────────────────────────────────────────────
    static void withdraw() {
        System.out.print("Enter Account ID: ");
        String id = scanner.nextLine().trim();
        double amount = readDouble("Enter Withdrawal Amount: ");

        try {
            BankAccount account = bank.getAccount(id);
            account.withdraw(amount);
        } catch (AccountNotFoundException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    // ─── 4. Transfer ─────────────────────────────────────────────────────────
    static void transfer() {
        System.out.print("From Account ID: ");
        String fromId = scanner.nextLine().trim();

        System.out.print("To Account ID: ");
        String toId = scanner.nextLine().trim();

        double amount = readDouble("Enter Transfer Amount: ");

        try {
            bank.transfer(fromId, toId, amount);
            System.out.println("Transfer successful.");
        } catch (AccountNotFoundException | InsufficientFundsException | InvalidAmountException e) {
            System.out.println("Transfer FAILED: " + e.getMessage());
        }
    }

    // ─── 5. View All Accounts ────────────────────────────────────────────────
    static void viewAllAccounts() {
        System.out.println("\n--- All Accounts ---");
        if (bank.getAllAccounts().isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        bank.getAllAccounts().forEach(System.out::println);
    }

    // ─── 6. View Transaction Log ─────────────────────────────────────────────
    static void viewTransactionLog() {
        System.out.println("\n--- Transaction Log ---");
        List<String> log = bank.getTransactionLog();
        if (log.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        log.forEach(System.out::println);
    }

    // ─── 7. Apply Interest ───────────────────────────────────────────────────
    static void applyInterest() {
        System.out.print("Enter Savings Account ID: ");
        String id = scanner.nextLine().trim();

        try {
            BankAccount account = bank.getAccount(id);
            if (account instanceof SavingsAccount sa) {
                sa.applyInterest();
            } else {
                System.out.println("ERROR: Account " + id + " is not a Savings Account.");
            }
        } catch (AccountNotFoundException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    // ─── 8. Simulate Teller Threads ──────────────────────────────────────────
    static void simulateTellers() throws InterruptedException {
        if (bank.getAllAccounts().isEmpty()) {
            System.out.println("No accounts to simulate. Create at least one account first.");
            return;
        }

        int numTellers = (int) readDouble("Enter number of teller threads (e.g. 5): ");

        List<String> accountIds = new ArrayList<>();
        bank.getAllAccounts().forEach(a -> accountIds.add(a.getAccountId()));

        System.out.println("\nStarting " + numTellers + " teller threads...\n");

        ExecutorService pool = Executors.newFixedThreadPool(numTellers);
        for (int i = 1; i <= numTellers; i++) {
            pool.submit(new TellerTask(bank, "Teller-" + i, accountIds));
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("\nAll tellers finished.");
        bank.printSummary();
    }

    // ─── Helper: safe double input ───────────────────────────────────────────
    static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
}
