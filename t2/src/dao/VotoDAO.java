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

    /**
     * Registra um voto de um usuário em um conteúdo (Post ou Comentário).
     * @param usuario O usuário que está votando.
     * @param conteudo O conteúdo que está recebendo o voto.
     * @param tipoVoto 1 para upvote, -1 para downvote.
     * @return true se o voto foi registrado, false caso contrário (ex: já votou).
     */
    public boolean registrarVoto(Usuario usuario, Conteudo conteudo, int tipoVoto) {
        // Primeiro, deleta qualquer voto anterior do usuário neste conteúdo
        String sqlDelete = "DELETE FROM voto WHERE fk_usuario = ? AND ";
        sqlDelete += "POST".equals(conteudo.getTipo()) ? "fk_post = ?" : "fk_comentario = ?";

        try (PreparedStatement pstmDelete = connection.prepareStatement(sqlDelete)) {
            pstmDelete.setInt(1, usuario.getId());
            pstmDelete.setInt(2, conteudo.getId());
            pstmDelete.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao limpar voto antigo.", e);
        }

        // Insere o novo voto
        String sqlInsert = "INSERT INTO voto (fk_usuario, fk_post, fk_comentario, tipo_voto) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmInsert = connection.prepareStatement(sqlInsert)) {
            pstmInsert.setInt(1, usuario.getId());
            if ("POST".equals(conteudo.getTipo())) {
                pstmInsert.setInt(2, conteudo.getId());
                pstmInsert.setNull(3, java.sql.Types.INTEGER);
            } else { // Comentário
                pstmInsert.setNull(2, java.sql.Types.INTEGER);
                pstmInsert.setInt(3, conteudo.getId());
            }
            pstmInsert.setInt(4, tipoVoto);
            pstmInsert.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Pode dar erro de chave única se a lógica de delete falhar, mas é um fallback.
            System.err.println("Não foi possível registrar o novo voto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calcula o total de upvotes e downvotes para um conteúdo e o atualiza.
     * @param conteudo O conteúdo a ser atualizado.
     */
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

        // Persiste a nova contagem na tabela de post ou comentario
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
