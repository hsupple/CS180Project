public class InsufficientFundsException extends Exception {

  public InsufficientFundsException() {
    super("Insufficient funds to complete the transaction.");
  }

  public InsufficientFundsException(String message) {
    super(message);
  }
}