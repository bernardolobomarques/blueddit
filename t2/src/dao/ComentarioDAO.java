package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import modelo.Comentario;
import modelo.Usuario;
import modelo.Post;
import modelo.Sublueddit;

public class ComentarioDAO implements BaseDAO {

    private Connection connection;

    public ComentarioDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Comentario)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Comentario.");
        }
        Comentario comentario = (Comentario) objeto;
        try {
            String sql = "INSERT INTO comentario (fk_usuario, fk_post, texto, data_publicacao, upvote, downvote) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstm.setInt(1, comentario.getAutor().getId());
                pstm.setInt(2, comentario.getPost().getId());
                pstm.setString(3, comentario.getTexto());
                pstm.setTimestamp(4, Timestamp.valueOf(comentario.getDataCriacao()));
                pstm.setInt(5, comentario.getUpvoteCount());
                pstm.setInt(6, comentario.getDownvoteCount());
                pstm.execute();

                try (ResultSet rst = pstm.getGeneratedKeys()) {
                    if (rst.next()) {
                        comentario.setId(rst.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object buscarPorId(int id) {
        return null;
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        return new ArrayList<>();
    }

    public ArrayList<Comentario> listarComentariosPorPost(Post post) {
        ArrayList<Comentario> comentarios = new ArrayList<>();
        try {
            String sql = "SELECT c.id, c.texto, c.data_publicacao, c.upvote, c.downvote, u.id AS autor_id, u.nome AS autor_nome FROM comentario AS c JOIN usuario AS u ON c.fk_usuario = u.id WHERE c.fk_post = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, post.getId());
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        Usuario autor = new Usuario(rst.getString("autor_nome"));
                        autor.setId(rst.getInt("autor_id"));

                        LocalDateTime data = rst.getTimestamp("data_publicacao").toLocalDateTime();
                        int upvotes = rst.getInt("upvote");
                        int downvotes = rst.getInt("downvote");

                        Comentario c = new Comentario(rst.getString("texto"), autor, post, data, upvotes, downvotes);
                        c.setId(rst.getInt("id"));
                        comentarios.add(c);
                    }
                }
            }
            return comentarios;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Object> listarTodosEagerLoading() {
        return listarTodosLazyLoading();
    }

    @Override
    public void atualizar(Object objeto) {
        if (!(objeto instanceof Comentario)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Comentario.");
        }
        Comentario comentario = (Comentario) objeto;
        try {
            String sql = "UPDATE comentario SET texto = ?, upvote = ?, downvote = ? WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, comentario.getTexto());
                pstm.setInt(2, comentario.getUpvoteCount());
                pstm.setInt(3, comentario.getDownvoteCount());
                pstm.setInt(4, comentario.getId());
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        try {
            String sql = "DELETE FROM comentario WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, id);
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
