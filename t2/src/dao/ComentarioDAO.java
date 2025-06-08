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
import modelo.Sublueddit;

public class ComentarioDAO implements BaseDAO {

    private Connection connection;

    // Construtor agora recebe a conexão
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
            String sql = "INSERT INTO comentario (fk_usuario, fk_post, texto) VALUES (?, ?, ?)";
            try (PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstm.setInt(1, comentario.getAutor().getId());
                pstm.setInt(2, comentario.getPost().getId());
                pstm.setString(3, comentario.getTexto());
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
        // Implementação simplificada para evitar complexidade desnecessária
        return null;
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
                        Usuario tempUser = new Usuario("temp");
                        tempUser.setId(rst.getInt("fk_usuario"));

                        Usuario placeholderPostAuthor = new Usuario("placeholder");
                        Sublueddit placeholderSublueddit = new Sublueddit("placeholder");
                        Post tempPost = new Post(placeholderPostAuthor, placeholderSublueddit, "", "temp post", 0, 0);
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
        return listarTodosLazyLoading();
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
}
