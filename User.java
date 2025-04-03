import java.util.List;

public class User implements UserInterface {

    public String getUserId() {}
    public String getUsername() {}
    public void setUsername(String username) {}

    public boolean login(String passwordInput) {}
    public void logout() {}
    public boolean checkPassword(String input) {}
    public void changePassword(String newPassword) {}
    public void setPassword(String password) {}

    public String getEmail() {}
    public void setEmail(String newEmail) {}
    public String getPhoneNumber() {}
    public void setPhoneNumber(String phone) {}
    public String getProfileSummary() {} //

    public void deleteAccount() {}
    public boolean isSeller() {}
    public boolean isBuyer() {}

    public double getBalance() {}
    public void depositFunds(double amount) {}
    public void withdrawFunds(double amount) throws InsufficientFundsException {}

    public void sendMessage(String recipientUsername, String content) {}
    public List<Message> getInbox(){} // Changed from List<String> to List<Message>
    public void deleteMessage(String messageId) {}

    public List<String> getTransactionHistory() {}
    public List<AuctionInterface> getActiveBids() {}
    public List<AuctionInterface> getWonAuctions() {}


}
