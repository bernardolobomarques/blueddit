package blueddit.dao;

import blueddit.model.Message;
import blueddit.model.User;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object para a entidade Message
 * Implementa o padrão DAO para isolar a camada de dados
 */
public class MessageDAO {
    private Connection connection;
    private UserDAO userDAO;

    public MessageDAO(Connection connection) {
        this.connection = connection;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Busca uma mensagem pelo ID
     */
    public Message findById(int id) throws SQLException {
        String sql = "SELECT * FROM messages WHERE message_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractMessage(rs);
                }
            }
        }
        return null;
    }

    /**
     * Busca mensagens por chat, ordenadas por pontuação
     */
    public List<Message> findByChat(int chatId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE message_chat = ? ORDER BY (message_upvotes - message_downvotes) DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, chatId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(extractMessage(rs));
                }
            }
        }
        return messages;
    }

    /**
     * Busca mensagens por usuário
     */
    public List<Message> findByUser(int userId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE message_user = ? ORDER BY message_id DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(extractMessage(rs));
                }
            }
        }
        return messages;
    }

    /**
     * Insere uma nova mensagem no banco de dados
     */
    public void insert(Message message) throws SQLException {
        String sql = "INSERT INTO messages (message_chat, message_user, message_content, message_upvotes, message_downvotes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getChatId());
            stmt.setInt(2, message.getUserId());
            stmt.setString(3, message.getContent());
            stmt.setInt(4, message.getUpvoters().size());
            stmt.setInt(5, message.getDownvoters().size());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    message.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Atualiza uma mensagem existente no banco de dados
     */
    public void update(Message message) throws SQLException {
        String sql = "UPDATE messages SET message_content = ?, message_upvotes = ?, message_downvotes = ? WHERE message_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, message.getContent());
            stmt.setInt(2, message.getUpvoters().size());
            stmt.setInt(3, message.getDownvoters().size());
            stmt.setInt(4, message.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Registra um voto positivo em uma mensagem
     */
    public void upvote(int messageId, int userId) throws SQLException {
        Message message = findById(messageId);
        if (message != null && message.upvote(userId)) {
            update(message);
        }
    }

    /**
     * Registra um voto negativo em uma mensagem
     */
    public void downvote(int messageId, int userId) throws SQLException {
        Message message = findById(messageId);
        if (message != null && message.downvote(userId)) {
            update(message);
        }
    }

    /**
     * Remove uma mensagem do banco de dados
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM messages WHERE message_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Extrai um objeto Message de um ResultSet
     */
    private Message extractMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("message_id"));
        message.setChatId(rs.getInt("message_chat"));
        message.setUserId(rs.getInt("message_user"));
        message.setContent(rs.getString("message_content"));

        // Carregar dados de votação
        List<Integer> upvoters = new ArrayList<>();
        List<Integer> downvoters = new ArrayList<>();

        // Em uma implementação real, você poderia ter uma tabela separada para votos
        // Aqui estamos simplificando para o propósito do projeto
        message.setUpvoters(upvoters);
        message.setDownvoters(downvoters);

        // Forçar a definição do número correto de votos com base no banco de dados
        for (int i = 0; i < rs.getInt("message_upvotes"); i++) {
            upvoters.add(-i - 1); // Usar IDs negativos artificiais apenas para ilustrar o conceito
        }

        for (int i = 0; i < rs.getInt("message_downvotes"); i++) {
            downvoters.add(-i - 1000); // Usar IDs negativos artificiais apenas para ilustrar o conceito
        }

        return message;
    }
}
