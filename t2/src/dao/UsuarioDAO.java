package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import modelo.Usuario;
import modelo.Post;
import modelo.Sublueddit;
import bd.ConexaoSQL;

public class UsuarioDAO implements BaseDAO {

    private Connection connection;

    // Construtor agora recebe a conexão
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
        try {
            String sql = "SELECT id, nome FROM usuario WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, id);
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    if (rst.next()) {
                        Usuario u = new Usuario(rst.getString("nome"));
                        u.setId(rst.getInt("id"));
                        return u;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        ArrayList<Object> usuarios = new ArrayList<>();
        try {
            String sql = "SELECT id, nome FROM usuario";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        Usuario u = new Usuario(rst.getString("nome"));
                        u.setId(rst.getInt("id"));
                        usuarios.add(u);
                    }
                }
            }
            return usuarios;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Object> listarTodosEagerLoading() {
        ArrayList<Object> usuarios = new ArrayList<>();
        Usuario ultimoUsuario = null;

        try {
            String sql = "SELECT u.id AS user_id, u.nome AS user_nome, " +
                    "p.id AS post_id, p.data_publicacao, p.descricao, p.upvote, p.downvote, " +
                    "s.id AS sub_id, s.nome AS sub_nome " +
                    "FROM usuario AS u " +
                    "LEFT JOIN post AS p ON u.id = p.fk_usuario " +
                    "LEFT JOIN sublueddit AS s ON p.fk_sublueddit = s.id " +
                    "ORDER BY u.id, p.id";

            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        if (ultimoUsuario == null || ultimoUsuario.getId() != rst.getInt("user_id")) {
                            int u_id = rst.getInt("user_id");
                            String u_nome = rst.getString("user_nome");
                            ultimoUsuario = new Usuario(u_nome);
                            ultimoUsuario.setId(u_id);
                            usuarios.add(ultimoUsuario);
                        }

                        int postId = rst.getInt("post_id");
                        if (postId != 0) {
                            Sublueddit sublueddit = null;
                            int subId = rst.getInt("sub_id");
                            if (subId != 0) {
                                sublueddit = new Sublueddit(rst.getString("sub_nome"));
                                sublueddit.setId(subId);
                            }

                            Post post = new Post(ultimoUsuario, sublueddit, rst.getString("data_publicacao"),
                                    rst.getString("descricao"), rst.getInt("upvote"), rst.getInt("downvote"));
                            post.setId(postId);

                            ultimoUsuario.addPost(post);
                        }
                    }
                }
            }
            return usuarios;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
