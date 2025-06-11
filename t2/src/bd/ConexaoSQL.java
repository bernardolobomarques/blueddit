package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoSQL {

    public static Connection recuperaConexao() {
        try {
            String sgbd = "mysql";
            String endereco = "localhost";
            String bd = "blueddit_db";
            String usuario = "root";
            String senha = "admin";

            Class.forName("com.mysql.cj.jdbc.Driver"); 

            Connection connection = DriverManager.getConnection(
                    "jdbc:" + sgbd + "://" + endereco + "/" + bd, usuario, senha);

            System.out.println("Conexão com o banco de dados estabelecida com sucesso!");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver JDBC não encontrado. Certifique-se de que o JAR do driver MySQL está no classpath.");
            throw new RuntimeException("Driver JDBC não encontrado.", e);
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            throw new RuntimeException("Erro de conexão com o banco de dados.", e);
        }
    }

    public static void fechaConexao(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexão com o banco de dados fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão com o banco de dados: " + e.getMessage());
            throw new RuntimeException("Erro ao fechar conexão.", e);
        }
    }
}