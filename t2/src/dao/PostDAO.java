package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Post;
import modelo.Usuario;
import modelo.Sublueddit;
import modelo.Comentario;
import bd.ConexaoSQL;

public class PostDAO implements BaseDAO {

    private Connection connection;

    public PostDAO() {
        this.connection = ConexaoSQL.recuperaConexao(); // Obtenha a conexão
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Post)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Post.");
        }
        Post post = (Post) objeto;
        try {
            String sql = "INSERT INTO post (fk_usuario, fk_subreddit, data_publicacao, descricao, upvote, downvote) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstm.setInt(1, post.getUsuario().getId()); // Supondo que Usuario tenha getId()
                pstm.setInt(2, post.getSublueddit().getId()); // Supondo que Post tenha getSubreddit() e Subreddit tenha getId()
                pstm.setString(3, post.getDataPublicada());
                pstm.setString(4, post.getDescricao());
                pstm.setInt(5, post.getUpvote());
                pstm.setInt(6, post.getDownvote());
                pstm.execute();

                try (ResultSet rst = pstm.getGeneratedKeys()) {
                    while (rst.next()) {
                        post.setId(rst.getInt(1)); // Supondo que Post tenha setId
                    }
                }
                // Salvar comentários se houverem
                ComentarioDAO cdao = new ComentarioDAO();
                for (Comentario comentario : post.getComentarios()) {
                    cdao.salvar(comentario); // O métodos salvar em ComentarioDAO precisará do post.getId()
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object buscarPorId(int id) {
        // Implementação lazy, sem carregar comentários ou usuário/subreddit completo
        try {
            String sql = "SELECT id, fk_usuario, fk_subreddit, data_publicacao, descricao, upvote, downvote FROM post WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, id);
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    if (rst.next()) {
                        // Você precisaria de DAOs para Usuario e Subreddit para carregar os objetos completos
                        // Para este exemplo, faremos um carregamento mínimo.
                        UsuarioDAO uDao = new UsuarioDAO();
                        Usuario usuario = (Usuario) uDao.buscarPorId(rst.getInt("fk_usuario"));

                        SubluedditDAO srDao = new SubluedditDAO(); // Se for Sublueddit, mude aqui.
                        Sublueddit subreddit = (Sublueddit) srDao.buscarPorId(rst.getInt("fk_subreddit")); // Se for Sublueddit, mude aqui.

                        Post post = new Post(usuario, rst.getString("data_publicacao"), rst.getString("descricao"),
                                rst.getInt("upvote"), rst.getInt("downvote"), null);
                        post.setId(rst.getInt("id"));
                        // Carregar comentários também, se necessário (Eager Loading para o post)
                        ComentarioDAO cdao = new ComentarioDAO();
                        for(Object obj : cdao.listarComentariosPorPost(post)) { // Supondo um novo método em ComentarioDAO
                            post.adicionarComentario((Comentario) obj);
                        }

                        return post;
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
        ArrayList<Object> posts = new ArrayList<>();
        try {
            String sql = "SELECT id, fk_usuario, fk_subreddit, data_publicacao, descricao, upvote, downvote FROM post";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        // Carregamento lazy: apenas IDs para FKs
                        Usuario tempUser = new Usuario("temp"); // Crie um objeto temporário ou busque apenas o ID
                        tempUser.setId(rst.getInt("fk_usuario"));

                        Sublueddit tempSub = new Sublueddit("temp"); // Crie um objeto temporário
                        tempSub.setId(rst.getInt("fk_subreddit"));

                        Post p = new Post(tempUser, rst.getString("data_publicacao"), rst.getString("descricao"),
                                rst.getInt("upvote"), rst.getInt("downvote"), null);
                        p.setId(rst.getInt("id"));
                        p.setSublueddit(tempSub); // Supondo que Post tenha setSubreddit
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
        ArrayList<Object> posts = new ArrayList<>();
        Post ultimoPost = null;
        Comentario ultimoComentario = null;

        try {
            String sql = "SELECT p.id AS post_id, p.data_publicacao, p.descricao, p.upvote, p.downvote, " +
                    "u.id AS user_id, u.nome AS user_nome, " +
                    "s.id AS sub_id, s.nome AS sub_nome, " +
                    "c.id AS comment_id, c.texto AS comment_texto, cu.id AS comment_user_id, cu.nome AS comment_user_nome " +
                    "FROM post AS p " +
                    "JOIN usuario AS u ON p.fk_usuario = u.id " +
                    "JOIN subreddit AS s ON p.fk_subreddit = s.id " + // Se for Sublueddit, mude aqui.
                    "LEFT JOIN comentario AS c ON p.id = c.fk_post " +
                    "LEFT JOIN usuario AS cu ON c.fk_usuario = cu.id " +
                    "ORDER BY p.id, c.id";

            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        if (ultimoPost == null || ultimoPost.getId() != rst.getInt("post_id")) {
                            int p_id = rst.getInt("post_id");
                            String p_data = rst.getString("data_publicacao");
                            String p_descricao = rst.getString("descricao");
                            int p_upvote = rst.getInt("upvote");
                            int p_downvote = rst.getInt("downvote");

                            Usuario postUsuario = new Usuario(rst.getString("user_nome"));
                            postUsuario.setId(rst.getInt("user_id"));

                            Sublueddit postSubreddit = new Sublueddit(rst.getString("sub_nome")); // Se for Sublueddit, mude aqui.
                            postSubreddit.setId(rst.getInt("sub_id"));

                            ultimoPost = new Post(postUsuario, p_data, p_descricao, p_upvote, p_downvote, null);
                            ultimoPost.setId(p_id);
                            ultimoPost.setSublueddit(postSubreddit); // Supondo setSubreddit em Post.java
                            posts.add(ultimoPost);
                            ultimoComentario = null; // Reinicia comentários para o novo post
                        }

                        if (rst.getInt("comment_id") != 0 && (ultimoComentario == null || ultimoComentario.getId() != rst.getInt("comment_id"))) {
                            int c_id = rst.getInt("comment_id");
                            String c_texto = rst.getString("comment_texto");

                            Usuario commentAutor = new Usuario(rst.getString("comment_user_nome"));
                            commentAutor.setId(rst.getInt("comment_user_id"));

                            Comentario comentario = new Comentario(c_texto, commentAutor, ultimoPost);
                            comentario.setId(c_id); // Supondo setId em Comentario.java
                            ultimoPost.adicionarComentario(comentario);
                            ultimoComentario = comentario;
                        }
                    }
                }
            }
            return posts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
                pstm.setInt(2, post.getUpvote());
                pstm.setInt(3, post.getDownvote());
                pstm.setInt(4, post.getId());
                pstm.executeUpdate();

                // Lógica para atualizar comentários, se necessário.
                // Isso pode ser complexo (identificar comentários novos, removidos, ou modificados).
                // Para simplificar, pode-se excluir e reinserir todos os comentários ou ter um DAO de comentário separado.
                ComentarioDAO cdao = new ComentarioDAO();
                // Exemplo: excluir todos os comentários existentes e reinserir os da lista do objeto Post
                // cdao.excluirComentariosPorPost(post.getId()); // Seria um novo método em ComentarioDAO
                // for (Comentario c : post.getComentarios()) {
                //     cdao.salvar(c); // O salvar em ComentarioDAO precisaria do post.getId()
                // }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        try {
            // Cuidado: Excluir um post pode exigir que os comentários relacionados sejam excluídos primeiro,
            // dependendo das suas restrições no BD.
            String sql = "DELETE FROM post WHERE id = ?";
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