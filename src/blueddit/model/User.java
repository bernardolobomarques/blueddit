package blueddit.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
    private int id;
    private String username;
    private String name;
    private String password;
    private Set<Chat> chats;  // Relação N:N bidirecional com Chat

    public User() {
        this.chats = new HashSet<>();
    }

    public User(int id, String username, String name, String password) {
        this();
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;
    }

    // Métodos para gerenciar a relação bidirecional com Chat
    public boolean addChat(Chat chat) {
        boolean added = chats.add(chat);
        if (added) {
            chat.addUser(this);  // Mantém a consistência bidirecional
        }
        return added;
    }

    public boolean removeChat(Chat chat) {
        boolean removed = chats.remove(chat);
        if (removed) {
            chat.removeUser(this);  // Mantém a consistência bidirecional
        }
        return removed;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Métodos para senha
    public String getPassword() {
        return this.password;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Método que retorna apenas o conjunto de chats (sem permitir modificação direta)
    public Set<Chat> getChats() {
        return new HashSet<>(chats);
    }

    // Método auxiliar para o DAO
    public String getChatIdsAsString() {
        StringBuilder sb = new StringBuilder();
        for (Chat chat : chats) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(chat.getId());
        }
        return sb.toString();
    }

    public void setChatIdsFromString(String chatIds) {
        // Este método será usado pelo DAO para carregar chats temporariamente até que ChatDAO possa completar o relacionamento
        this.chats.clear();

        if (chatIds != null && !chatIds.isEmpty()) {
            // Apenas armazenamos os IDs aqui; o ChatDAO irá carregar os objetos Chat completos posteriormente
            // Essa implementação é usada apenas pelo DAO e não deve ser chamada diretamente
        }
    }

    // Método para adicionar um chat diretamente (usado pelo DAO)
    public void addChatDirect(Chat chat) {
        if (chat != null) {
            this.chats.add(chat);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
