package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.Conteudo;
import modelo.Usuario;

public class VotoDAO {

    private Connection connection;

    public VotoDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean registrarVoto(Usuario usuario, Conteudo conteudo, int tipoVoto) {
        String sqlDelete = "DELETE FROM voto WHERE fk_usuario = ? AND ";
        sqlDelete += "POST".equals(conteudo.getTipo()) ? "fk_post = ?" : "fk_comentario = ?";

        try (PreparedStatement pstmDelete = connection.prepareStatement(sqlDelete)) {
            pstmDelete.setInt(1, usuario.getId());
            pstmDelete.setInt(2, conteudo.getId());
            pstmDelete.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao limpar voto antigo.", e);
        }

        String sqlInsert = "INSERT INTO voto (fk_usuario, fk_post, fk_comentario, tipo_voto) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmInsert = connection.prepareStatement(sqlInsert)) {
            pstmInsert.setInt(1, usuario.getId());
            if ("POST".equals(conteudo.getTipo())) {
                pstmInsert.setInt(2, conteudo.getId());
                pstmInsert.setNull(3, java.sql.Types.INTEGER);
            } else {
                pstmInsert.setNull(2, java.sql.Types.INTEGER);
                pstmInsert.setInt(3, conteudo.getId());
            }
            pstmInsert.setInt(4, tipoVoto);
            pstmInsert.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Não foi possível registrar o novo voto: " + e.getMessage());
            return false;
        }
    }


    public void atualizarContagemVotos(Conteudo conteudo) {
        String countSql = "SELECT " +
                "SUM(CASE WHEN tipo_voto = 1 THEN 1 ELSE 0 END) AS upvotes, " +
                "SUM(CASE WHEN tipo_voto = -1 THEN 1 ELSE 0 END) AS downvotes " +
                "FROM voto WHERE ";
        countSql += "POST".equals(conteudo.getTipo()) ? "fk_post = ?" : "fk_comentario = ?";

        int upvotes = 0;
        int downvotes = 0;

        try (PreparedStatement pstm = connection.prepareStatement(countSql)) {
            pstm.setInt(1, conteudo.getId());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    upvotes = rs.getInt("upvotes");
                    downvotes = rs.getInt("downvotes");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar votos.", e);
        }

        conteudo.setUpvoteCount(upvotes);
        conteudo.setDownvoteCount(downvotes);

        String updateSql = "UPDATE " + ("POST".equals(conteudo.getTipo()) ? "post" : "comentario") +
                " SET upvote = ?, downvote = ? WHERE id = ?";
        try (PreparedStatement pstmUpdate = connection.prepareStatement(updateSql)) {
            pstmUpdate.setInt(1, upvotes);
            pstmUpdate.setInt(2, downvotes);
            pstmUpdate.setInt(3, conteudo.getId());
            pstmUpdate.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar contagem de votos no conteúdo.", e);
        }
    }
}
