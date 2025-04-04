package src.NewDesign;

public class Message1 {
        private String id;
        private String senderId;
        private String recipientId;
        private String content;
        private String timestamp;

        public Message1(String id, String senderId, String recipientId, String content, String timestamp) {
                this.id = id;
                this.senderId = senderId;
                this.recipientId = recipientId;
                this.content = content;
                this.timestamp = timestamp;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getSenderId() {
                return senderId;
        }

        public void setSenderId(String senderId) {
                this.senderId = senderId;
        }

        public String getRecipientId() {
                return recipientId;
        }

        public void setRecipientId(String recipientId) {
                this.recipientId = recipientId;
        }

        public String getContent() {
                return content;
        }

        public void setContent(String content) {
                this.content = content;
        }

        public String getTimestamp() {
                return timestamp;
        }

        public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
        }
}
