package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import modelo.Sublueddit;
import modelo.Post;
import modelo.Usuario;
import modelo.Comentario;

public class SubluedditDAO implements BaseDAO {

    private Connection connection;

    public SubluedditDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Sublueddit)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Sublueddit.");
        }
        Sublueddit sublueddit = (Sublueddit) objeto;
        try {
            String sql = "INSERT INTO sublueddit (nome) VALUES (?)";
            try (PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstm.setString(1, sublueddit.getNome());
                pstm.execute();

                try (ResultSet rst = pstm.getGeneratedKeys()) {
                    if (rst.next()) {
                        sublueddit.setId(rst.getInt(1));
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

    @Override
    public ArrayList<Object> listarTodosEagerLoading() {
        Map<Integer, Sublueddit> subluedditMap = new HashMap<>();
        Map<Integer, Post> postMap = new HashMap<>();
        Map<Integer, Usuario> usuarioMap = new HashMap<>();

        String sql = "SELECT s.id AS sub_id, s.nome AS sub_nome, " +
                "p.id AS post_id, p.descricao, p.data_publicacao, p.upvote, p.downvote, " +
                "u_post.id AS post_autor_id, u_post.nome AS post_autor_nome, " +
                "c.id AS com_id, c.texto AS com_texto, c.data_publicacao as com_data, c.upvote as com_up, c.downvote as com_down, " +
                "u_com.id AS com_autor_id, u_com.nome AS com_autor_nome " +
                "FROM sublueddit AS s " +
                "LEFT JOIN post AS p ON s.id = p.fk_sublueddit " +
                "LEFT JOIN usuario AS u_post ON p.fk_usuario = u_post.id " +
                "LEFT JOIN comentario AS c ON p.id = c.fk_post " +
                "LEFT JOIN usuario AS u_com ON c.fk_usuario = u_com.id " +
                "ORDER BY s.id, p.id, c.id";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.execute();
            try (ResultSet rst = pstm.getResultSet()) {
                while (rst.next()) {
                    int subId = rst.getInt("sub_id");
                    Sublueddit sublueddit = subluedditMap.get(subId);
                    if (sublueddit == null) {
                        sublueddit = new Sublueddit(rst.getString("sub_nome"));
                        sublueddit.setId(subId);
                        subluedditMap.put(subId, sublueddit);
                    }

                    int postId = rst.getInt("post_id");
                    if (postId != 0) {
                        Post post = postMap.get(postId);
                        if (post == null) {
                            int autorPostId = rst.getInt("post_autor_id");
                            Usuario autorPost = usuarioMap.get(autorPostId);
                            if (autorPost == null) {
                                autorPost = new Usuario(rst.getString("post_autor_nome"));
                                autorPost.setId(autorPostId);
                                usuarioMap.put(autorPostId, autorPost);
                            }

                            LocalDateTime dataPost = rst.getTimestamp("data_publicacao").toLocalDateTime();
                            post = new Post(autorPost, sublueddit, rst.getString("descricao"), dataPost,
                                    rst.getInt("upvote"), rst.getInt("downvote"));
                            post.setId(postId);
                            postMap.put(postId, post);
                            sublueddit.adicionarPost(post);
                        }

                        int comId = rst.getInt("com_id");
                        if (comId != 0 && post.getComentarios().stream().noneMatch(c -> c.getId() == comId)) {
                            int autorComId = rst.getInt("com_autor_id");
                            Usuario autorComentario = usuarioMap.get(autorComId);
                            if (autorComentario == null) {
                                autorComentario = new Usuario(rst.getString("com_autor_nome"));
                                autorComentario.setId(autorComId);
                                usuarioMap.put(autorComId, autorComentario);
                            }

                            LocalDateTime dataCom = rst.getTimestamp("com_data").toLocalDateTime();
                            Comentario comentario = new Comentario(rst.getString("com_texto"), autorComentario, post,
                                    dataCom, rst.getInt("com_up"), rst.getInt("com_down"));
                            comentario.setId(comId);
                            post.adicionarComentario(comentario);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar sublueddits (Eager Loading).", e);
        }
        return new ArrayList<>(subluedditMap.values());
    }


    @Override
    public void atualizar(Object objeto) {
        if (!(objeto instanceof Sublueddit)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Sublueddit.");
        }
        Sublueddit sublueddit = (Sublueddit) objeto;
        try {
            String sql = "UPDATE sublueddit SET nome = ? WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, sublueddit.getNome());
                pstm.setInt(2, sublueddit.getId());
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        try {
            String sql = "DELETE FROM sublueddit WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, id);
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir sublueddit. Verifique as dependências (posts).", e);
        }
    }
}
