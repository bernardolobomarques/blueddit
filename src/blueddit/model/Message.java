package blueddit.model;

public class Message extends Content {
    private int chatId;
    private String content;

    public Message() {
        super();
    }

    public Message(int id, int userId, int chatId, String content) {
        super(id, userId);
        this.chatId = chatId;
        this.content = content;
    }

    // Implementação dos métodos abstratos da classe Content
    @Override
    public String getType() {
        return "MESSAGE";
    }

    @Override
    public String getPreview() {
        if (content.length() <= 50) {
            return content;
        }
        return content.substring(0, 47) + "...";
    }

    // Getters e Setters específicos
    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", score=" + getScore() +
                ", content='" + getPreview() + '\'' +
                '}';
    }
}
