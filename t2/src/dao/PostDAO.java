package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import modelo.Post;
import modelo.Usuario;
import modelo.Sublueddit;

public class PostDAO implements BaseDAO {

    private Connection connection;

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
            // Corrigido a ordem e o tipo dos parâmetros
            String sql = "INSERT INTO post (fk_usuario, fk_sublueddit, descricao, data_publicacao, upvote, downvote) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstm.setInt(1, post.getAutor().getId());
                pstm.setInt(2, post.getSublueddit().getId());
                pstm.setString(3, post.getTexto()); // Usar getTexto() de Conteudo
                pstm.setTimestamp(4, Timestamp.valueOf(post.getDataCriacao())); // Converter LocalDateTime para Timestamp
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
        // A lógica de Eager Loading já lida com buscas mais complexas.
        return null;
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        ArrayList<Object> posts = new ArrayList<>();
        try {
            String sql = "SELECT id, fk_usuario, fk_sublueddit, descricao, data_publicacao, upvote, downvote FROM post";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        Usuario tempUser = new Usuario("temp"); // Lazy: objeto placeholder
                        tempUser.setId(rst.getInt("fk_usuario"));

                        Sublueddit tempSub = new Sublueddit("temp"); // Lazy: objeto placeholder
                        tempSub.setId(rst.getInt("fk_sublueddit"));

                        // Corrigido para usar o novo construtor e converter a data
                        LocalDateTime dataPublicacao = rst.getTimestamp("data_publicacao").toLocalDateTime();
                        Post p = new Post(
                                tempUser,
                                tempSub,
                                rst.getString("descricao"),
                                dataPublicacao,
                                rst.getInt("upvote"),
                                rst.getInt("downvote"));

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
        // Para simplificar, o Eager Loading é feito nos DAOs de Sublueddit e Usuario
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
                pstm.setString(1, post.getTexto()); // Usar getTexto() de Conteudo
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
            // A exclusão em cascata no banco de dados já lida com os comentários e votos
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
