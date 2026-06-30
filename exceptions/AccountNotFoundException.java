package exceptions;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String accountId) {
        super("Account not found: " + accountId);
    }
}
