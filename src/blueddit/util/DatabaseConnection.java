package blueddit.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados
 */
public class DatabaseConnection {
    // Configurações da conexão com o banco MySQL
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/blueddit";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root"; // Altere para sua senha do MySQL

    private static Connection connection;

    /**
     * Obtém uma conexão com o banco de dados
     * @return Objeto Connection representando a conexão com o banco
     * @throws SQLException Se ocorrer um erro ao conectar ao banco
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Registrando o driver do MySQL explicitamente
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Criando a conexão com o banco de dados
                connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                System.out.println("Conectado ao MySQL com sucesso!");

                // Inicializa o banco de dados (cria tabelas se necessário)
                initDatabase();
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL não encontrado: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Inicializa o banco de dados criando as tabelas necessárias e inserindo dados iniciais
     * @throws SQLException Se ocorrer um erro durante a inicialização
     */
    private static void initDatabase() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Criar tabela de usuários se não existir
            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id_user INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username_user VARCHAR(50) UNIQUE NOT NULL, " +
                    "name_user VARCHAR(100) NOT NULL, " +
                    "password_user VARCHAR(100) NOT NULL, " +
                    "chats_user VARCHAR(255)" +
                    ")");

            // Criar tabela de chats se não existir
            statement.execute("CREATE TABLE IF NOT EXISTS chats (" +
                    "chat_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "chat_users VARCHAR(255), " +
                    "chat_messages VARCHAR(255), " +
                    "chat_title VARCHAR(100), " +
                    "chat_date_of_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Criar tabela de mensagens se não existir
            statement.execute("CREATE TABLE IF NOT EXISTS messages (" +
                    "message_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "message_chat INT, " +
                    "message_upvotes INT DEFAULT 0, " +
                    "message_downvotes INT DEFAULT 0, " +
                    "message_user INT, " +
                    "message_content TEXT, " +
                    "message_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (message_chat) REFERENCES chats(chat_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (message_user) REFERENCES users(id_user) ON DELETE CASCADE" +
                    ")");

            // Verificar se existem dados na tabela users
            java.sql.ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            int userCount = rs.getInt(1);

            // Se não há usuários, insere dados de exemplo
            if (userCount == 0) {
                System.out.println("Inserindo dados de exemplo...");

                // Inserir usuários de exemplo
                statement.execute("INSERT INTO users (username_user, name_user, password_user) VALUES " +
                        "('admin', 'Administrador', 'admin123'), " +
                        "('joao', 'João Silva', 'senha123'), " +
                        "('maria', 'Maria Oliveira', 'senha456')");

                // Inserir chats de exemplo
                statement.execute("INSERT INTO chats (chat_users, chat_title) VALUES " +
                        "(',1,2,', 'Discussão de Tecnologia'), " +
                        "(',1,3,', 'Notícias do dia'), " +
                        "(',2,3,', 'Entretenimento')");

                // Inserir mensagens de exemplo
                statement.execute("INSERT INTO messages (message_chat, message_user, message_content) VALUES " +
                        "(1, 1, 'Bem-vindo ao Blueddit! Este é um espaço para discutir tecnologia.'), " +
                        "(1, 2, 'Obrigado, estou gostando da plataforma. O que vocês acham de IA?'), " +
                        "(2, 3, 'Olá mundo! Quais são as notícias mais importantes de hoje?'), " +
                        "(3, 2, 'Qual filme vocês recomendam assistir este fim de semana?'), " +
                        "(3, 3, 'Assisti \"Duna: Parte 2\" e recomendo muito!')");

                // Atualizar alguns upvotes e downvotes para exemplo
                statement.execute("UPDATE messages SET message_upvotes = 5 WHERE message_id = 2");
                statement.execute("UPDATE messages SET message_upvotes = 3, message_downvotes = 1 WHERE message_id = 5");
            }
        }
    }

    /**
     * Fecha a conexão com o banco de dados
     * @throws SQLException Se ocorrer um erro ao fechar a conexão
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Conexão com o banco de dados fechada.");
        }
    }
}
