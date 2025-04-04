/**
 * Defines platform-wide financial transaction methods.
 */
public interface Payment {

    double getUserBalance(String username);
    void depositFunds(String username, double amount);
    void withdrawFunds(String username, double amount) throws InsufficientFundsException;

    boolean processPayment(String buyerUsername, String sellerUsername, double amount)
            throws InsufficientFundsException;

    void recordTransaction(String sender, String recipient, double amount);
}