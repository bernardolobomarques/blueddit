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
import modelo.Usuario;
import modelo.Post;
import modelo.Sublueddit;

public class UsuarioDAO implements BaseDAO {

    private Connection connection;

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Usuario)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Usuario.");
        }
        Usuario usuario = (Usuario) objeto;
        try {
            String sql = "INSERT INTO usuario (nome) VALUES (?)";
            try (PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstm.setString(1, usuario.getNome());
                pstm.execute();

                try (ResultSet rst = pstm.getGeneratedKeys()) {
                    if (rst.next()) {
                        usuario.setId(rst.getInt(1));
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
        Map<Integer, Usuario> usuarioMap = new HashMap<>();
        Map<Integer, Sublueddit> subluedditMap = new HashMap<>();

        String sql = "SELECT u.id AS user_id, u.nome AS user_nome, " +
                "p.id AS post_id, p.descricao, p.data_publicacao, p.upvote, p.downvote, " +
                "s.id AS sub_id, s.nome AS sub_nome " +
                "FROM usuario AS u " +
                "LEFT JOIN post AS p ON u.id = p.fk_usuario " +
                "LEFT JOIN sublueddit AS s ON p.fk_sublueddit = s.id " +
                "ORDER BY u.id";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.execute();
            try (ResultSet rst = pstm.getResultSet()) {
                while (rst.next()) {
                    int userId = rst.getInt("user_id");
                    Usuario usuario = usuarioMap.get(userId);
                    if (usuario == null) {
                        usuario = new Usuario(rst.getString("user_nome"));
                        usuario.setId(userId);
                        usuarioMap.put(userId, usuario);
                    }

                    int postId = rst.getInt("post_id");
                    if (postId != 0 && usuario.getPosts().stream().noneMatch(p -> p.getId() == postId)) {
                        int subId = rst.getInt("sub_id");
                        Sublueddit sublueddit = subluedditMap.get(subId);
                        if(sublueddit == null) {
                            sublueddit = new Sublueddit(rst.getString("sub_nome"));
                            sublueddit.setId(subId);
                            subluedditMap.put(subId, sublueddit);
                        }

                        LocalDateTime dataPost = rst.getTimestamp("data_publicacao").toLocalDateTime();
                        Post post = new Post(usuario, sublueddit, rst.getString("descricao"),
                                dataPost, rst.getInt("upvote"), rst.getInt("downvote"));
                        post.setId(postId);
                        usuario.addPost(post);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar usuários e posts (Eager Loading).", e);
        }
        return new ArrayList<>(usuarioMap.values());
    }


    @Override
    public void atualizar(Object objeto) {
        if (!(objeto instanceof Usuario)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Usuario.");
        }
        Usuario usuario = (Usuario) objeto;
        try {
            String sql = "UPDATE usuario SET nome = ? WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, usuario.getNome());
                pstm.setInt(2, usuario.getId());
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        try {
            String sql = "DELETE FROM usuario WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, id);
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
