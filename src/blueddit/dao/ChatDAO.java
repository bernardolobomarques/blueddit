package blueddit.dao;

import blueddit.model.Chat;
import blueddit.model.Message;
import blueddit.model.User;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object para a entidade Chat
 * Implementa o padrão DAO para isolar a camada de dados
 */
public class ChatDAO {
    private Connection connection;
    private UserDAO userDAO;
    private MessageDAO messageDAO;

    public ChatDAO(Connection connection) {
        this.connection = connection;
    }

    // Métodos para injetar os DAOs relacionados e evitar dependência circular
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setMessageDAO(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    /**
     * Busca um chat pelo ID
     */
    public Chat findById(int id) throws SQLException {
        String sql = "SELECT * FROM chats WHERE chat_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Chat chat = extractChat(rs);
                    loadChatRelationships(chat);
                    return chat;
                }
            }
        }
        return null;
    }

    /**
     * Busca todos os chats
     */
    public List<Chat> findAll() throws SQLException {
        List<Chat> chats = new ArrayList<>();
        String sql = "SELECT * FROM chats ORDER BY chat_date_of_creation DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Chat chat = extractChat(rs);
                loadChatRelationships(chat);
                chats.add(chat);
            }
        }
        return chats;
    }

    /**
     * Busca chats que contêm um determinado usuário
     */
    public List<Chat> findByUser(int userId) throws SQLException {
        List<Chat> chats = new ArrayList<>();
        String sql = "SELECT * FROM chats WHERE chat_users LIKE ? ORDER BY chat_date_of_creation DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%," + userId + ",%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Chat chat = extractChat(rs);
                    loadChatRelationships(chat);
                    chats.add(chat);
                }
            }
        }
        return chats;
    }

    /**
     * Insere um novo chat no banco de dados
     */
    public void insert(Chat chat) throws SQLException {
        String sql = "INSERT INTO chats (chat_users, chat_messages, chat_title) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Adicionamos vírgulas antes e depois para facilitar a busca com LIKE
            stmt.setString(1, "," + chat.getUserIdsAsString() + ",");
            stmt.setString(2, chat.getMessageIdsAsString());
            stmt.setString(3, chat.getTitle());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    chat.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Atualiza um chat existente no banco de dados
     */
    public void update(Chat chat) throws SQLException {
        String sql = "UPDATE chats SET chat_users = ?, chat_messages = ?, chat_title = ? WHERE chat_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Adicionamos vírgulas antes e depois para facilitar a busca com LIKE
            stmt.setString(1, "," + chat.getUserIdsAsString() + ",");
            stmt.setString(2, chat.getMessageIdsAsString());
            stmt.setString(3, chat.getTitle());
            stmt.setInt(4, chat.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Remove um chat do banco de dados
     */
    public void delete(int id) throws SQLException {
        // Primeiro, remova as mensagens associadas ao chat
        if (messageDAO != null) {
            List<Message> messages = messageDAO.findByChat(id);
            for (Message message : messages) {
                messageDAO.delete(message.getId());
            }
        }

        // Depois, remova o chat
        String sql = "DELETE FROM chats WHERE chat_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Adiciona uma mensagem a um chat
     */
    public void addMessageToChat(Chat chat, Message message) throws SQLException {
        // Adiciona a mensagem ao chat
        chat.addMessage(message);

        // Atualiza o chat no banco de dados
        update(chat);
    }

    /**
     * Extrai um objeto Chat de um ResultSet
     */
    private Chat extractChat(ResultSet rs) throws SQLException {
        Chat chat = new Chat();
        chat.setId(rs.getInt("chat_id"));
        chat.setTitle(rs.getString("chat_title") != null ? rs.getString("chat_title") : "Chat #" + chat.getId());
        chat.setDateOfCreation(rs.getTimestamp("chat_date_of_creation"));
        return chat;
    }

    /**
     * Carrega os relacionamentos de um chat (usuários e mensagens)
     */
    private void loadChatRelationships(Chat chat) throws SQLException {
        loadChatUsers(chat);
        loadChatMessages(chat);
    }

    /**
     * Carrega os usuários de um chat
     */
    private void loadChatUsers(Chat chat) throws SQLException {
        if (userDAO != null) {
            String userIdsStr = getChatUserIdsFromDb(chat.getId());
            if (userIdsStr != null && !userIdsStr.isEmpty()) {
                String[] userIds = userIdsStr.replace(",", " ").trim().split("\\s+");
                for (String idStr : userIds) {
                    if (!idStr.isEmpty()) {
                        try {
                            int userId = Integer.parseInt(idStr);
                            User user = userDAO.findById(userId);
                            if (user != null) {
                                chat.addUser(user);
                            }
                        } catch (NumberFormatException e) {
                            // Ignora IDs inválidos
                        }
                    }
                }
            }
        }
    }

    /**
     * Carrega as mensagens de um chat
     */
    private void loadChatMessages(Chat chat) throws SQLException {
        if (messageDAO != null) {
            List<Message> messages = messageDAO.findByChat(chat.getId());
            for (Message message : messages) {
                chat.addMessage(message);
            }
        }
    }

    /**
     * Obtém a string de IDs de usuários de um chat do banco de dados
     */
    private String getChatUserIdsFromDb(int chatId) throws SQLException {
        String sql = "SELECT chat_users FROM chats WHERE chat_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, chatId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("chat_users");
                }
            }
        }
        return "";
    }
}
