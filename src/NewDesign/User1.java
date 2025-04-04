package src.NewDesign;

public interface User1 {
    String getId();
    String getUsername();
    String getPassword();
    String getRole(); // "Buyer" or "Seller"
    void setPassword(String password);
}
