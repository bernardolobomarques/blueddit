package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import modelo.Usuario;
import modelo.Post;
import bd.ConexaoSQL; // Importe a sua classe de conexão

public class UsuarioDAO implements BaseDAO {

    private Connection connection;

    // O construtor deve obter a conexão aqui
    public UsuarioDAO() { // Remova o parâmetro Connection connection
        this.connection = ConexaoSQL.recuperaConexao(); // Obtenha a conexão
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
                    while (rst.next()) {
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
                        return new Usuario(rst.getString("nome")); // Supondo construtor Usuario(String nome)
                        // e que você vai adicionar setId no modelo Usuario
                        // para setar o ID do banco.
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
                        Usuario u = new Usuario(rst.getString("nome")); // Supondo construtor Usuario(String nome)
                        u.setId(rst.getInt("id")); // Supondo que Usuario tenha setId
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
            // Este JOIN busca usuários e seus posts.
            // Se Post.java tiver um ID e setters para ele.
            String sql = "SELECT u.id, u.nome, p.id, p.data_publicada, p.descricao, p.upvote, p.downvote " +
                    "FROM usuario AS u " +
                    "LEFT JOIN post AS p ON u.id = p.fk_usuario " +
                    "ORDER BY u.id, p.id";

            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                try (ResultSet rst = pstm.getResultSet()) {
                    while (rst.next()) {
                        if (ultimoUsuario == null || ultimoUsuario.getId() != rst.getInt(1)) {
                            int u_id = rst.getInt(1);
                            String u_nome = rst.getString(2);
                            ultimoUsuario = new Usuario(u_nome);
                            ultimoUsuario.setId(u_id);
                            usuarios.add(ultimoUsuario);
                        }
                        // Adiciona post apenas se houver dados de post na linha (LEFT JOIN pode ter null)
                        if (rst.getInt(3) != 0) { // Verifica se o ID do post não é 0 (ou null, dependendo do driver)
                            // Assumindo que Post tenha um construtor que aceita todos esses parâmetros e setId.
                            Post post = new Post(ultimoUsuario, rst.getString(4), rst.getString(5), rst.getInt(6), rst.getInt(7), null); // O último 'null' seria para comentário, mas posts não são criados com um único comentário
                            post.setId(rst.getInt(3)); // Supondo que Post tenha setId
                            // Adicionar o post à lista de posts do usuário.
                            // Isso exigiria um método addPost(Post) na classe Usuario.
                            ultimoUsuario.getPosts().add(post); // Supondo que Usuario.getPosts() retorne a lista e não null.
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
                pstm.setInt(2, usuario.getId()); // Supondo que Usuario tenha getId
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        try {
            // Cuidado: Excluir um usuário pode exigir que os posts e comentários relacionados sejam excluídos primeiro,
            // ou que suas FKs sejam setadas para NULL, dependendo da sua regra de negócio e restrições no BD.
            // Para simplificar, estamos excluindo apenas o usuário aqui.
            String sql = "DELETE FROM usuario WHERE id = ?";
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