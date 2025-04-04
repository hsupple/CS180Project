import java.time.LocalDateTime;

/**
 * Represents a message sent between users.
 */
public class Message {
    private final String messageId;
    private final String sender;
    private final String recipient;
    private final String content;
    private boolean isRead;
    private final LocalDateTime timestamp;

    public Message(String messageId, String sender, String recipient, String content) {
        this.messageId = messageId;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.isRead = false;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessageId() { return messageId; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getContent() { return content; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getTimestamp() { return timestamp; }
}