package exceptions;

public class InvalidAmountException extends Exception {
    public InvalidAmountException(double amount) {
        super("Invalid amount: ₹" + amount + ". Amount must be greater than zero.");
    }
}
