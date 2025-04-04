import java.util.List;

/**
 * Defines common user behaviors for all platform users.
 * Shared by both buyers and sellers.
 */
public interface UserInterface {

    // Identification
    String getUserId();
    String getUsername();
    void setUsername(String username);

    // Authentication
    boolean login(String passwordInput);
    void logout();
    boolean checkPassword(String input);
    void changePassword(String newPassword);
    void setPassword(String password);

    // Profile
    String getEmail();
    void setEmail(String newEmail);
    String getPhoneNumber();
    void setPhoneNumber(String phone);
    String getProfileSummary(); //

    // Account Management
    void deleteAccount();
    boolean isSeller();
    boolean isBuyer();

    // Financials
    double getBalance();
    void depositFunds(double amount);
    void withdrawFunds(double amount) throws InsufficientFundsException;

    // Messaging
    void sendMessage(String recipientUsername, String content);
    List<Message> getInbox(); // Changed from List<String> to List<Message>
    void deleteMessage(String messageId);

    // Auction Activity
    List<String> getTransactionHistory();
    List<AuctionInterface> getActiveBids();
    List<AuctionInterface> getWonAuctions();
}