package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import bd.ConexaoSQL;
import modelo.Sublueddit;
import modelo.Post;
import modelo.Usuario;
import modelo.Comentario;

public class SubluedditDAO implements BaseDAO {

    private Connection connection;

    // Construtor agora recebe a conexão
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
        try {
            String sql = "SELECT id, nome FROM sublueddit WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, id);
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    if (rst.next()) {
                        Sublueddit sr = new Sublueddit(rst.getString("nome"));
                        sr.setId(rst.getInt("id"));
                        return sr;
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
        ArrayList<Object> sublueddits = new ArrayList<>();
        try {
            String sql = "SELECT id, nome FROM sublueddit";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        Sublueddit sr = new Sublueddit(rst.getString("nome"));
                        sr.setId(rst.getInt("id"));
                        sublueddits.add(sr);
                    }
                }
            }
            return sublueddits;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Object> listarTodosEagerLoading() {
        ArrayList<Object> sublueddits = new ArrayList<>();
        java.util.Map<Integer, Sublueddit> subluedditMap = new java.util.HashMap<>();
        java.util.Map<Integer, Post> postMap = new java.util.HashMap<>();
        java.util.Map<Integer, Usuario> usuarioMap = new java.util.HashMap<>();

        String sql = "SELECT s.id AS sub_id, s.nome AS sub_nome, " +
                "p.id AS post_id, p.data_publicacao, p.descricao, p.upvote, p.downvote, " +
                "u_post.id AS post_autor_id, u_post.nome AS post_autor_nome, " +
                "c.id AS com_id, c.texto AS com_texto, " +
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
                    if (sublueddit == null && subId != 0) {
                        sublueddit = new Sublueddit(rst.getString("sub_nome"));
                        sublueddit.setId(subId);
                        subluedditMap.put(subId, sublueddit);
                        sublueddits.add(sublueddit);
                    }

                    int postId = rst.getInt("post_id");
                    if (postId != 0) {
                        Post post = postMap.get(postId);
                        if (post == null) {
                            int autorPostId = rst.getInt("post_autor_id");
                            Usuario autorPost = usuarioMap.get(autorPostId);
                            if (autorPost == null && autorPostId != 0) {
                                autorPost = new Usuario(rst.getString("post_autor_nome"));
                                autorPost.setId(autorPostId);
                                usuarioMap.put(autorPostId, autorPost);
                            }

                            post = new Post(autorPost, sublueddit, rst.getString("data_publicacao"),
                                    rst.getString("descricao"), rst.getInt("upvote"), rst.getInt("downvote"));
                            post.setId(postId);
                            postMap.put(postId, post);
                            if (sublueddit != null) {
                                sublueddit.adicionarPost(post);
                            }
                        }

                        int comId = rst.getInt("com_id");
                        if (comId != 0) {
                            boolean commentExists = post.getComentarios().stream().anyMatch(c -> c.getId() == comId);
                            if (!commentExists) {
                                int autorComId = rst.getInt("com_autor_id");
                                Usuario autorComentario = usuarioMap.get(autorComId);
                                if (autorComentario == null && autorComId != 0) {
                                    autorComentario = new Usuario(rst.getString("com_autor_nome"));
                                    autorComentario.setId(autorComId);
                                    usuarioMap.put(autorComId, autorComentario);
                                }

                                Comentario comentario = new Comentario(rst.getString("com_texto"), autorComentario, post);
                                comentario.setId(comId);
                                post.adicionarComentario(comentario);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sublueddits;
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
