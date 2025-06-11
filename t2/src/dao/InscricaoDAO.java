package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import modelo.Sublueddit;
import modelo.Usuario;

public class InscricaoDAO {

    private Connection connection;

    public InscricaoDAO(Connection connection) {
        this.connection = connection;
    }

    public void inscrever(Usuario usuario, Sublueddit sublueddit) {
        String sql = "INSERT INTO inscricao (fk_usuario, fk_sublueddit) VALUES (?, ?)";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, usuario.getId());
            pstm.setInt(2, sublueddit.getId());
            pstm.executeUpdate();
            System.out.println("Inscrição de '" + usuario.getNome() + "' em 'b/" + sublueddit.getNome() + "' realizada!");
        } catch (SQLException e) {
            System.err.println("Usuário já inscrito ou erro ao inscrever: " + e.getMessage());
        }
    }

    public void desinscrever(Usuario usuario, Sublueddit sublueddit) {
        String sql = "DELETE FROM inscricao WHERE fk_usuario = ? AND fk_sublueddit = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, usuario.getId());
            pstm.setInt(2, sublueddit.getId());
            int affectedRows = pstm.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Inscrição de '" + usuario.getNome() + "' em 'b/" + sublueddit.getNome() + "' removida!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover inscrição.", e);
        }
    }
}
