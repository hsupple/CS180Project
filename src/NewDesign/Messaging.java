package src.NewDesign;

import java.util.List;

public interface Messaging {
    void sendMessage(Message1 message);
    List<Message1> getMessages(String userId);
}