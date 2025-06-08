package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate; // Para posts
import java.util.ArrayList;

import bd.ConexaoSQL;
import modelo.Sublueddit;
import modelo.Sublueddit; // Se for Sublueddit, mude aqui.
import modelo.Post;
import modelo.Usuario; // Para Eager Loading de posts

public class SubluedditDAO implements BaseDAO {

    private Connection connection;

    public SubluedditDAO() {
        this.connection = ConexaoSQL.recuperaConexao();
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Sublueddit)) { // Se for Sublueddit, mude aqui.
            throw new IllegalArgumentException("Objeto deve ser do tipo Subreddit.");
        }
        Sublueddit subreddit = (Sublueddit) objeto; // Se for Sublueddit, mude aqui.
        try {
            String sql = "INSERT INTO subreddit (nome) VALUES (?)";
            try (PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstm.setString(1, subreddit.getNome());
                pstm.execute();

                try (ResultSet rst = pstm.getGeneratedKeys()) {
                    while (rst.next()) {
                        subreddit.setId(rst.getInt(1)); // Supondo que Subreddit tenha setId
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
            String sql = "SELECT id, nome FROM subreddit WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, id);
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    if (rst.next()) {
                        Sublueddit sr = new Sublueddit(rst.getString("nome")); // Supondo construtor Subreddit(String nome)
                        sr.setId(rst.getInt("id")); // Supondo que Subreddit tenha setId
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
        ArrayList<Object> subreddits = new ArrayList<>();
        try {
            String sql = "SELECT id, nome FROM subreddit";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        Sublueddit sr = new Sublueddit(rst.getString("nome"));
                        sr.setId(rst.getInt("id"));
                        subreddits.add(sr);
                    }
                }
            }
            return subreddits;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Object> listarTodosEagerLoading() {
        ArrayList<Object> subreddits = new ArrayList<>();
        Sublueddit ultimoSubreddit = null;
        // Para eager loading completo de posts (com seus usuários), precisaríamos de um mecanismo mais complexo
        // ou de múltiplas consultas. Para este exemplo, farei eager loading dos posts do subreddit.
        // Se precisar de usuários dos posts, o PostDAO já faria isso.

        try {
            String sql = "SELECT s.id, s.nome, p.id, p.data_publicada, p.descricao, p.upvote, p.downvote, u.id, u.nome " +
                    "FROM subreddit AS s " +
                    "LEFT JOIN post AS p ON s.id = p.fk_subreddit " +
                    "LEFT JOIN usuario AS u ON p.fk_usuario = u.id " +
                    "ORDER BY s.id, p.id";

            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        if (ultimoSubreddit == null || ultimoSubreddit.getId() != rst.getInt(1)) {
                            int s_id = rst.getInt(1);
                            String s_nome = rst.getString(2);
                            ultimoSubreddit = new Sublueddit(s_nome);
                            ultimoSubreddit.setId(s_id);
                            subreddits.add(ultimoSubreddit);
                        }

                        if (rst.getInt(3) != 0) { // Se houver um Post
                            int p_id = rst.getInt(3);
                            String p_data = rst.getString(4);
                            String p_descricao = rst.getString(5);
                            int p_upvote = rst.getInt(6);
                            int p_downvote = rst.getInt(7);

                            Usuario postUsuario = null;
                            if (rst.getInt(8) != 0) { // Se houver um Usuário para o Post
                                int u_id = rst.getInt(8);
                                String u_nome = rst.getString(9);
                                postUsuario = new Usuario(u_nome);
                                postUsuario.setId(u_id);
                            }
                            Post post = new Post(postUsuario, p_data, p_descricao, p_upvote, p_downvote, null);
                            post.setId(p_id);
                            ultimoSubreddit.getPosts().add(post); // Supondo um métodos getPosts() que retorna a lista
                        }
                    }
                }
            }
            return subreddits;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void atualizar(Object objeto) {
        if (!(objeto instanceof Sublueddit)) { // Se for Sublueddit, mude aqui.
            throw new IllegalArgumentException("Objeto deve ser do tipo Subreddit.");
        }
        Sublueddit subreddit = (Sublueddit) objeto; // Se for Sublueddit, mude aqui.
        try {
            String sql = "UPDATE subreddit SET nome = ? WHERE id = ?";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, subreddit.getNome());
                pstm.setInt(2, subreddit.getId());
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        try {
            // Excluir um subreddit pode exigir que os posts relacionados sejam excluídos primeiro,
            // ou que suas FKs sejam setadas para NULL.
            String sql = "DELETE FROM subreddit WHERE id = ?";
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