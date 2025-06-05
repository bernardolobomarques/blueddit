package blueddit.dao;

import blueddit.model.Chat;
import blueddit.model.User;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object para a entidade User
 * Implementa o padrão DAO para isolar a camada de dados
 */
public class UserDAO {
    private Connection connection;
    private ChatDAO chatDAO;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para injetar o ChatDAO e evitar dependência circular
    public void setChatDAO(ChatDAO chatDAO) {
        this.chatDAO = chatDAO;
    }

    /**
     * Autentica um usuário pelo nome de usuário e senha
     */
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username_user = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = extractUser(rs);
                    // Verifica a senha usando o método encapsulado
                    if (user.checkPassword(password)) {
                        loadUserChats(user);
                        return user;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Busca um usuário pelo ID
     */
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id_user = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = extractUser(rs);
                    loadUserChats(user);
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Busca um usuário pelo nome de usuário
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username_user = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = extractUser(rs);
                    loadUserChats(user);
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Busca todos os usuários
     */
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = extractUser(rs);
                loadUserChats(user);
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Insere um novo usuário no banco de dados
     */
    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username_user, name_user, password_user) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Atualiza um usuário existente no banco de dados
     */
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET username_user = ?, name_user = ?, password_user = ? WHERE id_user = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
        }

        // Atualizamos os relacionamentos com chats em uma operação separada
        updateUserChats(user);
    }

    /**
     * Atualiza os relacionamentos entre usuários e chats no banco de dados
     */
    private void updateUserChats(User user) throws SQLException {
        if (chatDAO == null) return;

        // Para cada chat do usuário, garantimos que o relacionamento está registrado no banco
        for (Chat chat : user.getChats()) {
            String sql = "SELECT chat_users FROM chats WHERE chat_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, chat.getId());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String chatUsers = rs.getString("chat_users");
                        if (chatUsers == null) chatUsers = "";

                        // Verifica se o ID do usuário já está na lista
                        if (!chatUsers.contains("," + user.getId() + ",")) {
                            // Adiciona o ID do usuário à lista
                            if (chatUsers.isEmpty()) {
                                chatUsers = "," + user.getId() + ",";
                            } else {
                                chatUsers += user.getId() + ",";
                            }

                            // Atualiza a lista de usuários do chat
                            String updateSql = "UPDATE chats SET chat_users = ? WHERE chat_id = ?";
                            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                                updateStmt.setString(1, chatUsers);
                                updateStmt.setInt(2, chat.getId());
                                updateStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Remove um usuário do banco de dados
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id_user = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Extrai um objeto User de um ResultSet
     */
    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id_user"));
        user.setUsername(rs.getString("username_user"));
        user.setName(rs.getString("name_user"));
        user.setPassword(rs.getString("password_user"));
        return user;
    }

    /**
     * Carrega os chats de um usuário
     */
    private void loadUserChats(User user) throws SQLException {
        if (chatDAO != null) {
            String sql = "SELECT chat_id FROM chats WHERE chat_users LIKE ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, "%," + user.getId() + ",%");
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int chatId = rs.getInt("chat_id");
                        Chat chat = chatDAO.findById(chatId);
                        if (chat != null) {
                            user.addChat(chat);
                        }
                    }
                }
            }
        }
    }
}
