package blueddit;

import blueddit.ui.BluedditCLI;
import blueddit.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe principal que inicia o sistema Blueddit
 * @author Seu Nome
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando Blueddit - Sistema de Crowdsourcing Inspirado no Reddit");
        System.out.println("=============================================================");

        try {
            // Estabelecer conexão com o banco de dados
            Connection connection = DatabaseConnection.getConnection();
            System.out.println("Conexão com banco de dados estabelecida com sucesso!");

            // Inicializar a interface de linha de comando
            BluedditCLI cli = new BluedditCLI(connection);
            cli.start();

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Tentativa de fechar a conexão com o banco de dados ao finalizar o programa
            try {
                DatabaseConnection.closeConnection();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão com banco de dados: " + e.getMessage());
            }
        }
    }
}