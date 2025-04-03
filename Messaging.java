import java.util.List;

/**
 * Defines user-to-user messaging system functionality.
 */
public interface Messaging {

    void sendMessage(String senderUsername, String recipientUsername, String content);
    List<Message> getInbox(String username);
    List<Message> getSentMessages(String username);

    void deleteMessage(String username, String messageId);
    void markAsRead(String username, String messageId);
    void markAsUnread(String username, String messageId);

    Message getMessageById(String messageId);
}
