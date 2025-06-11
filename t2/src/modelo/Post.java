package modelo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Post extends Conteudo {

    private Sublueddit sublueddit;
    private List<Comentario> comentarios;

    /**
     * Construtor para criar um NOVO post.
     */
    public Post(Usuario usuario, Sublueddit sublueddit, String descricao) {
        super(usuario, descricao); // Chama o construtor de 2 argumentos da classe pai
        if (usuario == null || sublueddit == null) {
            throw new IllegalArgumentException("Usuário e Sublueddit não podem ser nulos ao criar um Post.");
        }
        this.sublueddit = sublueddit;
        this.comentarios = new ArrayList<>();
    }

    /**
     * NOVO CONSTRUTOR para carregar um post do BANCO DE DADOS.
     */
    public Post(Usuario usuario, Sublueddit sublueddit, String descricao, LocalDateTime dataPublicacao, int upvote, int downvote) {
        super(usuario, descricao, dataPublicacao, upvote, downvote); // Chama o construtor de 5 argumentos da classe pai
        if (usuario == null || sublueddit == null) {
            throw new IllegalArgumentException("Usuário e Sublueddit não podem ser nulos ao criar um Post.");
        }
        this.sublueddit = sublueddit;
        this.comentarios = new ArrayList<>();
    }

    // Getters e Setters
    public Sublueddit getSublueddit() { return sublueddit; }
    public void setSublueddit(Sublueddit sublueddit) { this.sublueddit = sublueddit; }
    public List<Comentario> getComentarios() { return comentarios; }
    public void adicionarComentario(Comentario comentario) {
        if (comentario != null) {
            this.comentarios.add(comentario);
        }
    }

    @Override
    public String getTipo() {
        return "POST";
    }
}
