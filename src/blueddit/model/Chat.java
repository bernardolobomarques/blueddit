package blueddit.model;

import java.sql.Timestamp;
import java.util.*;

public class Chat {
    private int id;
    private Set<User> users;
    private List<Message> messages;
    private Timestamp dateOfCreation;
    private String title;

    public Chat() {
        this.users = new HashSet<>();
        this.messages = new ArrayList<>();
        this.dateOfCreation = new Timestamp(System.currentTimeMillis());
    }

    public Chat(int id, String title) {
        this();
        this.id = id;
        this.title = title;
    }

    // Métodos para gerenciar a relação bidirecional com User
    public boolean addUser(User user) {
        if (user == null) return false;
        // Evita chamada recursiva para manter a consistência bidirecional
        if (!users.contains(user)) {
            users.add(user);
            // Não chama user.addChat(this) se estamos sendo chamados por esse método
            if (!userContainsThisChat(user)) {
                user.addChat(this);
            }
            return true;
        }
        return false;
    }

    private boolean userContainsThisChat(User user) {
        // Verifica se o usuário já possui este chat
        return user.getChats().contains(this);
    }

    public boolean removeUser(User user) {
        boolean removed = users.remove(user);
        if (removed && user.getChats().contains(this)) {
            user.removeChat(this);
        }
        return removed;
    }

    // Métodos para gerenciar a relação composicional com Message (um chat tem muitas mensagens)
    public boolean addMessage(Message message) {
        // Um chat "possui" mensagens (composição)
        return messages.add(message);
    }

    public boolean removeMessage(Message message) {
        return messages.remove(message);
    }

    public Message findMessageById(int messageId) {
        for (Message message : messages) {
            if (message.getId() == messageId) {
                return message;
            }
        }
        return null;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<User> getUsers() {
        // Retorna uma cópia para proteger a encapsulação
        return new HashSet<>(users);
    }

    public List<Message> getMessages() {
        // Retorna uma cópia para proteger a encapsulação
        return new ArrayList<>(messages);
    }

    public void setMessages(List<Message> messages) {
        this.messages = new ArrayList<>(messages);
    }

    public Timestamp getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Timestamp dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Métodos auxiliares para o DAO
    public String getUserIdsAsString() {
        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(user.getId());
        }
        return sb.toString();
    }

    public String getMessageIdsAsString() {
        StringBuilder sb = new StringBuilder();
        for (Message message : messages) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(message.getId());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", users=" + users.size() +
                ", messages=" + messages.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return id == chat.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
