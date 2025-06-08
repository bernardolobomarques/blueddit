package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import modelo.Post;
import modelo.Usuario;
import modelo.Sublueddit;
import modelo.Comentario;
import bd.ConexaoSQL;

public class PostDAO implements BaseDAO {

    private Connection connection;

    // Construtor agora recebe a conexão
    public PostDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Post)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Post.");
        }
        Post post = (Post) objeto;
        try {
            String sql = "INSERT INTO post (fk_usuario, fk_sublueddit, data_publicacao, descricao, upvote, downvote) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstm.setInt(1, post.getUsuario().getId());
                pstm.setInt(2, post.getSublueddit().getId());
                pstm.setString(3, post.getDataPublicada());
                pstm.setString(4, post.getDescricao());
                pstm.setInt(5, post.getUpvoteCount());
                pstm.setInt(6, post.getDownvoteCount());
                pstm.execute();

                try (ResultSet rst = pstm.getGeneratedKeys()) {
                    if (rst.next()) {
                        post.setId(rst.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object buscarPorId(int id) {
        // Implementação simplificada, pois o Eager Loading principal já cuida disso.
        // Recriar toda a lógica de DAOs aqui seria ineficiente.
        return null;
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        ArrayList<Object> posts = new ArrayList<>();
        try {
            String sql = "SELECT id, fk_usuario, fk_sublueddit, data_publicacao, descricao, upvote, downvote FROM post";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        Usuario tempUser = new Usuario("temp");
                        tempUser.setId(rst.getInt("fk_usuario"));

                        Sublueddit tempSub = new Sublueddit("temp");
                        tempSub.setId(rst.getInt("fk_sublueddit"));

                        Post p = new Post(tempUser, tempSub, rst.getString("data_publicacao"), rst.getString("descricao"),
                                rst.getInt("upvote"), rst.getInt("downvote"));
                        p.setId(rst.getInt("id"));
                        posts.add(p);
                    }
                }
            }
            return posts;
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
        if (!(objeto instanceof Post)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Post.");
        }
        Post post = (Post) objeto;
        try {
            String sql = "UPDATE post SET descricao = ?, upvote = ?, downvote = ? WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, post.getDescricao());
                pstm.setInt(2, post.getUpvoteCount());
                pstm.setInt(3, post.getDownvoteCount());
                pstm.setInt(4, post.getId());
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        try {
            String sqlDeleteComentarios = "DELETE FROM comentario WHERE fk_post = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sqlDeleteComentarios)) {
                pstm.setInt(1, id);
                pstm.executeUpdate();
            }

            String sqlDeletePost = "DELETE FROM post WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sqlDeletePost)) {
                pstm.setInt(1, id);
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
