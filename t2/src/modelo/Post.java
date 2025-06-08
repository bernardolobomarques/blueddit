package modelo;

import java.util.List;
import java.util.ArrayList;

public class Post {
    private Usuario usuario;
    private String dataPublicada;
    private String descricao;
    private int upvote;
    private int downvote;
    private List<Comentario> comentarios;

    public Post(Usuario usuario, String dataPublicada, String descricao, int upvote, int downvote, Comentario comentario) {
        this.usuario = usuario;
        this.dataPublicada = dataPublicada;
        this.descricao = descricao;
        this.upvote = upvote;
        this.downvote = downvote;
        this.comentarios = new ArrayList<>();
    }

    public void adicionarComentario(Comentario comentario) {
        comentarios.add(comentario);
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

    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvaote) {
        this.upvote = upvaote;
    }

    public int getDownvote() {
        return downvote;
    }

    public void setDownvote(int downvote) {
        this.downvote = downvote;
    }

    public String getDataPublicada() {
        return dataPublicada;
    }
}









