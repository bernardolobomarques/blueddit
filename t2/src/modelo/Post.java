package modelo;

import java.util.List;
import java.util.ArrayList;

public class Post implements Voto {
    private int id;
    private Usuario usuario;
    private Sublueddit sublueddit;
    private String dataPublicada;
    private String descricao;
    private int upvote;
    private int downvote;
    private List<Comentario> comentarios;

    // Construtor principal e único. Garante que um Post sempre tenha o necessário.
    public Post(Usuario usuario, Sublueddit sublueddit, String dataPublicada, String descricao, int upvote, int downvote) {
        if (usuario == null || sublueddit == null) {
            throw new IllegalArgumentException("Usuário e Sublueddit não podem ser nulos ao criar um Post.");
        }
        this.usuario = usuario;
        this.sublueddit = sublueddit;
        this.dataPublicada = dataPublicada;
        this.descricao = descricao;
        this.upvote = upvote;
        this.downvote = downvote;
        this.comentarios = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Sublueddit getSublueddit() {
        return sublueddit;
    }

    public void setSublueddit(Sublueddit sublueddit) {
        this.sublueddit = sublueddit;
    }

    public void adicionarComentario(Comentario comentario) {
        if (comentario != null) {
            this.comentarios.add(comentario);
        }
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public void upvote() {
        this.upvote++;
    }

    @Override
    public void downvote() {
        this.downvote++;
    }

    @Override
    public int getUpvoteCount() {
        return this.upvote;
    }

    @Override
    public int getDownvoteCount() {
        return this.downvote;
    }

    public String getDataPublicada() {
        return dataPublicada;
    }
}
