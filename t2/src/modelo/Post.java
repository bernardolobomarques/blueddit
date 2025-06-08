package modelo;

import java.util.List;
import java.util.ArrayList;

public class Post implements Voto { // <-- Adicione "implements Votavel"
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

    @Override // <-- Adicione a anotação @Override
    public int getUpvoteCount() {
        return upvote;
    }

    @Override // <-- Adicione a anotação @Override
    public void upvote() {
        this.upvote++; // Incrementa diretamente
    }

    @Override // <-- Adicione a anotação @Override
    public int getDownvoteCount() {
        return downvote;
    }

    @Override // <-- Adicione a anotação @Override
    public void downvote() {
        this.downvote++; // Incrementa diretamente
    }

    // Os setters originais (setUpvote, setDownvote) podem ser mantidos ou removidos se você preferir
    // que os votos só sejam manipulados via upvote()/downvote()
    public void setUpvote(int upvote) { // Mantenha se ainda for útil em algum lugar
        this.upvote = upvote;
    }

    public void setDownvote(int downvote) { // Mantenha se ainda for útil em algum lugar
        this.downvote = downvote;
    }

    public String getDataPublicada() {
        return dataPublicada;
    }
}