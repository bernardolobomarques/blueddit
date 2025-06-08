package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import bd.ConexaoSQL;
import modelo.Comentario;
import modelo.Usuario;
import modelo.Post;

public class ComentarioDAO implements BaseDAO {

    private Connection connection;

    public ComentarioDAO() {
        this.connection = ConexaoSQL.recuperaConexao();
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Comentario)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Comentario.");
        }
        Comentario comentario = (Comentario) objeto;
        try {
            String sql = "INSERT INTO comentario (fk_usuario, fk_post, texto) VALUES (?, ?, ?)";
            try (PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstm.setInt(1, comentario.getAutor().getId()); // Supondo que Usuario tenha getId()
                pstm.setInt(2, comentario.getPost().getId()); // Supondo que Comentario tenha getPost() e Post tenha getId()
                pstm.setString(3, comentario.getTexto());
                pstm.execute();

                try (ResultSet rst = pstm.getGeneratedKeys()) {
                    while (rst.next()) {
                        comentario.setId(rst.getInt(1)); // Supondo que Comentario tenha setId
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object buscarPorId(int id) {
        // Implementação lazy, sem carregar usuário ou post completo
        try {
            String sql = "SELECT id, fk_usuario, fk_post, texto FROM comentario WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, id);
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    if (rst.next()) {
                        // Carga mínima de FKs. Para objetos completos, usar DAOs de Usuario e Post.
                        UsuarioDAO uDao = new UsuarioDAO();
                        Usuario autor = (Usuario) uDao.buscarPorId(rst.getInt("fk_usuario"));

                        PostDAO pDao = new PostDAO();
                        Post post = (Post) pDao.buscarPorId(rst.getInt("fk_post")); // Carrega o post, que por sua vez pode carregar mais coisas.

                        Comentario c = new Comentario(rst.getString("texto"), autor, post);
                        c.setId(rst.getInt("id")); // Supondo que Comentario tenha setId
                        return c;
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
        ArrayList<Object> comentarios = new ArrayList<>();
        try {
            String sql = "SELECT id, fk_usuario, fk_post, texto FROM comentario";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        // Carregamento lazy: apenas IDs para FKs
                        Usuario tempUser = new Usuario("temp");
                        tempUser.setId(rst.getInt("fk_usuario"));

                        Post tempPost = new Post(null, null, "temp", 0, 0, null);
                        tempPost.setId(rst.getInt("fk_post"));

                        Comentario c = new Comentario(rst.getString("texto"), tempUser, tempPost);
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

    public ArrayList<Object> listarComentariosPorPost(Post post) {
        ArrayList<Object> comentarios = new ArrayList<>();
        try {
            String sql = "SELECT c.id, c.texto, u.id AS autor_id, u.nome AS autor_nome FROM comentario AS c JOIN usuario AS u ON c.fk_usuario = u.id WHERE c.fk_post = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, post.getId());
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        Usuario autor = new Usuario(rst.getString("autor_nome"));
                        autor.setId(rst.getInt("autor_id"));
                        Comentario c = new Comentario(rst.getString("texto"), autor, post);
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
        ArrayList<Object> comentarios = new ArrayList<>();
        try {
            String sql = "SELECT c.id, c.texto, u.id AS autor_id, u.nome AS autor_nome, " +
                    "p.id AS post_id, p.descricao AS post_descricao " +
                    "FROM comentario AS c " +
                    "JOIN usuario AS u ON c.fk_usuario = u.id " +
                    "JOIN post AS p ON c.fk_post = p.id";

            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        Usuario autor = new Usuario(rst.getString("autor_nome"));
                        autor.setId(rst.getInt("autor_id"));

                        // Carregamento mínimo do Post para evitar loop infinito de Eager Loading (Post -> Comentarios -> Post -> ...)
                        Post postReferenciado = new Post(null, null, rst.getString("post_descricao"), 0, 0, null);
                        postReferenciado.setId(rst.getInt("post_id"));

                        Comentario c = new Comentario(rst.getString("texto"), autor, postReferenciado);
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
    public void atualizar(Object objeto) {
        if (!(objeto instanceof Comentario)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Comentario.");
        }
        Comentario comentario = (Comentario) objeto;
        try {
            String sql = "UPDATE comentario SET texto = ? WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, comentario.getTexto());
                pstm.setInt(2, comentario.getId());
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

    public void closeConnection() {
        ConexaoSQL.fechaConexao(this.connection);
    }
}